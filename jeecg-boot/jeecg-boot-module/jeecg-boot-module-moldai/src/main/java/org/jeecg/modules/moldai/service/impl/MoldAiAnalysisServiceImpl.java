package org.jeecg.modules.moldai.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.moldai.algorithm.VTTCalculator;
import org.jeecg.modules.moldai.entity.MoldAiDeviceBinding;
import org.jeecg.modules.moldai.entity.MoldAiRiskResult;
import org.jeecg.modules.moldai.entity.MoldAiScene;
import org.jeecg.modules.moldai.mapper.MoldAiDeviceBindingMapper;
import org.jeecg.modules.moldai.mapper.MoldAiRiskResultMapper;
import org.jeecg.modules.moldai.mapper.MoldAiSceneMapper;
import org.jeecg.modules.moldai.platform.ThingsBoardClient;
import org.jeecg.modules.moldai.service.IMoldAiAnalysisService;
import org.jeecg.modules.moldai.service.ITelemetryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MoldAiAnalysisServiceImpl implements IMoldAiAnalysisService {
    
    @Autowired
    private VTTCalculator calculator;
    
    @Autowired
    private ITelemetryService telemetryService;

    @Autowired
    private ThingsBoardClient thingsBoardClient;
    
    @Autowired
    private MoldAiSceneMapper sceneMapper;
    
    @Autowired
    private MoldAiDeviceBindingMapper bindingMapper;
    
    @Autowired
    private MoldAiRiskResultMapper resultMapper;
    
    @Override
    public MoldAiRiskResult analyze(String deviceId) {
        log.info("开始分析设备: {}", deviceId);
        
        // 1. 获取设备绑定的场景
        MoldAiDeviceBinding binding = bindingMapper.selectByDeviceId(deviceId);
        if (binding == null) {
            log.warn("设备[{}]未在霉菌模块绑定，跳过分析", deviceId);
            return null;
        }
        
        MoldAiScene scene = sceneMapper.selectById(binding.getSceneId());
        if (scene == null) {
            log.error("设备[{}]绑定的场景ID[{}]不存在", deviceId, binding.getSceneId());
            return null;
        }
        
        // 2. 从遥测服务获取7天历史数据 (使用1h聚合)
        Instant endTime = Instant.now();
        Instant startTime = endTime.minus(7, ChronoUnit.DAYS);
        
        // 重点：传入 "1h" 作为聚合参数
        List<VTTCalculator.DataPoint> dataPoints = telemetryService.queryTelemetry(
            deviceId, startTime.toEpochMilli(), endTime.toEpochMilli(), "1h");
            
        if (dataPoints.isEmpty()) {
            log.warn("设备[{}]在过去7天无温湿度数据，无法计算", deviceId);
            return null;
        }
        
        log.info("获取到设备[{}]的历史数据: {}条", deviceId, dataPoints.size());
        
        // 3. 执行VTT算法
        VTTCalculator.SceneParam param = new VTTCalculator.SceneParam();
        param.setMaterialLevel(scene.getMaterialLevel());
        param.setThresholdLow(scene.getThresholdLow());
        param.setThresholdHigh(scene.getThresholdHigh());
        
        VTTCalculator.RiskResult calcResult = calculator.calculate(
            deviceId, dataPoints, param);
        
        // 4. 保存结果到MySQL (业务结果)
        MoldAiRiskResult result = new MoldAiRiskResult();
        result.setDeviceId(deviceId);
        result.setSceneId(scene.getId());
        result.setMiValue(calcResult.getMiValue());
        result.setRiskLevel(calcResult.getRiskLevel());
        result.setCalculatedTime(new Date());
        resultMapper.insert(result);
        
        // 5. 推送结果 (可选：推回TB或TDengine)
        telemetryService.pushResult(deviceId, calcResult);

        // 6. 智能联动：风险过高时触发车位锁
        if ("HIGH".equals(result.getRiskLevel())) {
            log.info("设备[{}]霉菌风险等级为HIGH(MI={})，触发车位锁控制", deviceId, result.getMiValue());
            // 构造RPC参数，具体协议需根据设备定义，这里假设发送 set_lock 指令
            Map<String, Object> params = new HashMap<>();
            params.put("lock", false); // 假设 false 代表降下车位锁/打开通路
            params.put("triggered_by", "mold_ai_risk_high");
            
            thingsBoardClient.sendRpcCommand(deviceId, "set_lock", params);
        }
        
        return result;
    }

    @Override
    public MoldAiRiskResult getLatest(String deviceId) {
        return resultMapper.selectLatestByDeviceId(deviceId);
    }
}
