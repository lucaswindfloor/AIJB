package com.moldai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.moldai.algorithm.VTTCalculator;
import com.moldai.entity.MoldAiDeviceBinding;
import com.moldai.entity.MoldAiRiskResult;
import com.moldai.entity.MoldAiScene;
import com.moldai.mapper.MoldAiDeviceBindingMapper;
import com.moldai.mapper.MoldAiRiskResultMapper;
import com.moldai.mapper.MoldAiSceneMapper;
import com.moldai.service.AlarmService;
import com.moldai.service.IDeviceControlService;
import com.moldai.service.IMoldAiAnalysisService;
import com.moldai.service.ITelemetryService;
import lombok.extern.slf4j.Slf4j;
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
    
    // 为了保证能控制到真实设备，这里硬编码了地锁ID (仅供演示)
    private static final String LOCK_DEVICE_ID = "7dc08ac0-4c15-11f0-bda4-570db53547bd";

    @Autowired
    private VTTCalculator calculator;
    
    @Autowired
    private ITelemetryService telemetryService;

    @Autowired
    private AlarmService alarmService;

    @Autowired
    private IDeviceControlService deviceControlService;
    
    @Autowired
    private MoldAiSceneMapper sceneMapper;
    
    @Autowired
    private MoldAiDeviceBindingMapper bindingMapper;
    
    @Autowired
    private MoldAiRiskResultMapper resultMapper;

    @org.springframework.beans.factory.annotation.Value("${mold-ai.algorithm.window-days:7}")
    private int windowDays;
    
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
        
        // 2. 从遥测服务获取历史数据
        Instant endTime = Instant.now();
        Instant startTime = endTime.minus(windowDays, ChronoUnit.DAYS);
        
        log.info("Querying data from {} to {} (Window: {} days)", startTime, endTime, windowDays);
        
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

        // 6. 告警与控制逻辑
        // 【逻辑修改】：只在高风险时触发，移除强制触发逻辑
        log.info("Checking control logic for risk level: {}", result.getRiskLevel());
        handleRiskResponse(deviceId, scene, result, false); 
        
        return result;
    }

    private void handleRiskResponse(String deviceId, MoldAiScene scene, MoldAiRiskResult result, boolean forceTrigger) {
        String level = result.getRiskLevel();
        double mi = result.getMiValue().doubleValue();

        // 只要是 HIGH 或者 开启了强制触发，都执行
        if ("HIGH".equals(level) || forceTrigger) {
            // 1. 发送紧急告警
            String msg = String.format("设备 [%s] 处于高风险状态！(MI: %.2f, 场景: %s)", 
                                     deviceId, mi, scene.getSceneName());
            alarmService.sendAlarm("高风险预警", msg);

            // 2. 自动触发控制
            log.info("Triggering CONTROL for device [{}] due to HIGH risk.", deviceId);
            
            Map<String, String> params = new HashMap<>();
            // 实际业务：开启除湿机/排风扇
            // params.put("power", "on"); 
            // deviceControlService.sendRpcCommand(deviceId, "enable_dehumidifier", params);
            
            // 【当前演示】：触发地锁作为验证
            params.put("lockControl", "01"); 
            
            boolean success = deviceControlService.sendRpcCommand(LOCK_DEVICE_ID, "LockControl", params);
            
            if (success) {
                log.info("Auto-control command sent successfully.");
            } else {
                log.error("Failed to send auto-control command.");
            }

        } else if ("MEDIUM".equals(level)) {
            // 1. 发送普通告警
            String msg = String.format("设备 [%s] 风险升高，请注意。(MI: %.2f, 场景: %s)", 
                                     deviceId, mi, scene.getSceneName());
            alarmService.sendAlarm("中度风险提示", msg);
            
            // 中风险暂时不自动控制，或者只开启轻微通风
        }
    }

    @Override
    public MoldAiRiskResult getLatest(String deviceId) {
        return resultMapper.selectLatestByDeviceId(deviceId);
    }

    @Override
    public List<MoldAiScene> getSceneList() {
        return sceneMapper.selectList(new LambdaQueryWrapper<MoldAiScene>()
                .eq(MoldAiScene::getEnabled, 1)
                .orderByAsc(MoldAiScene::getId));
    }

    @Override
    public List<MoldAiRiskResult> getHistory(String deviceId, int limit) {
        return resultMapper.selectHistory(deviceId, limit);
    }

    @Override
    public void manualControlLock(String command) {
        log.info("Manual Lock Control Triggered: Command={}", command);
        Map<String, String> params = new HashMap<>();
        params.put("lockControl", command);
        
        boolean success = deviceControlService.sendRpcCommand(LOCK_DEVICE_ID, "LockControl", params);
        if (success) {
            log.info("Manual lock command sent successfully.");
        } else {
            log.error("Failed to send manual lock command.");
        }
    }
}
