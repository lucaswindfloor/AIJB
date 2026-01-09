package com.moldai.service.impl;

import com.moldai.algorithm.VTTCalculator;
import com.moldai.entity.MoldAiDeviceBinding;
import com.moldai.entity.MoldAiRiskResult;
import com.moldai.entity.MoldAiScene;
import com.moldai.mapper.MoldAiDeviceBindingMapper;
import com.moldai.mapper.MoldAiRiskResultMapper;
import com.moldai.mapper.MoldAiSceneMapper;
import com.moldai.service.IMoldAiAnalysisService;
import com.moldai.service.ITelemetryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class MoldAiAnalysisServiceImpl implements IMoldAiAnalysisService {
    
    @Autowired
    private VTTCalculator calculator;
    
    @Autowired
    private ITelemetryService telemetryService;
    
    @Autowired
    private MoldAiSceneMapper sceneMapper;
    
    @Autowired
    private MoldAiDeviceBindingMapper bindingMapper;
    
    @Autowired
    private MoldAiRiskResultMapper resultMapper;
    
    @Override
    public MoldAiRiskResult analyze(String deviceId) {
        log.info("Starting analysis for device: {}", deviceId);
        
        // 1. 获取设备绑定的场景
        MoldAiDeviceBinding binding = bindingMapper.selectByDeviceId(deviceId);
        if (binding == null) {
            log.warn("Device [{}] not bound to any scene, skipping analysis", deviceId);
            return null;
        }
        
        MoldAiScene scene = sceneMapper.selectById(binding.getSceneId());
        if (scene == null) {
            log.error("Scene ID [{}] not found for device [{}]", binding.getSceneId(), deviceId);
            return null;
        }
        
        // 2. 从遥测服务获取7天历史数据 (使用1h聚合)
        Instant endTime = Instant.now();
        Instant startTime = endTime.minus(7, ChronoUnit.DAYS);
        
        List<VTTCalculator.DataPoint> dataPoints = telemetryService.queryTelemetry(
            deviceId, startTime.toEpochMilli(), endTime.toEpochMilli(), "1h");
            
        if (dataPoints.isEmpty()) {
            log.warn("No telemetry data found for device [{}] in last 7 days", deviceId);
            return null;
        }
        
        log.info("Fetched {} data points for device [{}]", dataPoints.size(), deviceId);
        
        // 3. 执行VTT算法
        VTTCalculator.SceneParam param = new VTTCalculator.SceneParam();
        param.setMaterialLevel(scene.getMaterialLevel());
        param.setThresholdLow(scene.getThresholdLow());
        param.setThresholdHigh(scene.getThresholdHigh());
        
        VTTCalculator.RiskResult calcResult = calculator.calculate(
            deviceId, dataPoints, param);
        
        // 4. 保存结果到MySQL
        MoldAiRiskResult result = new MoldAiRiskResult();
        result.setDeviceId(deviceId);
        result.setSceneId(scene.getId());
        result.setMiValue(calcResult.getMiValue());
        result.setRiskLevel(calcResult.getRiskLevel());
        result.setCalculatedTime(new Date());
        
        resultMapper.insert(result);
        
        // 5. 推送结果 (可选)
        telemetryService.pushResult(deviceId, calcResult);
        
        return result;
    }

    @Override
    public MoldAiRiskResult getLatest(String deviceId) {
        return resultMapper.selectLatestByDeviceId(deviceId);
    }
}


