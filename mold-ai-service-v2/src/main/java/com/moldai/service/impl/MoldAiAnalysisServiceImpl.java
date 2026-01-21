package com.moldai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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

import java.math.BigDecimal;
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
            deviceId, startTime.toEpochMilli(), endTime.toEpochMilli(), "30m");
            
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
        
        // 4. 保存结果到MySQL (避免重复插入相同时间点的数据)
        MoldAiRiskResult result = new MoldAiRiskResult();
        result.setDeviceId(deviceId);
        result.setSceneId(scene.getId());
        result.setMiValue(calcResult.getMiValue());
        result.setRiskLevel(calcResult.getRiskLevel());
        if (!dataPoints.isEmpty()) {
            VTTCalculator.DataPoint latest = dataPoints.get(dataPoints.size() - 1);
            result.setTemperature(BigDecimal.valueOf(latest.getTemperature()));
            result.setHumidity(BigDecimal.valueOf(latest.getHumidity()));
            // Use the timestamp of the latest data point for accuracy
            result.setCalculatedTime(new Date(latest.getTimestamp()));
        } else {
            // Fallback to current time if for some reason list is empty but we proceed (shouldn't happen due to check above)
            result.setCalculatedTime(new Date());
        }
        
        // Check for duplicates
        Long count = resultMapper.selectCount(new LambdaQueryWrapper<MoldAiRiskResult>()
                .eq(MoldAiRiskResult::getDeviceId, deviceId)
                .eq(MoldAiRiskResult::getCalculatedTime, result.getCalculatedTime()));
                
        if (count == 0) {
            resultMapper.insert(result);
            log.info("Analysis result saved for device [{}] at [{}]", deviceId, result.getCalculatedTime());
        } else {
            log.info("Analysis result skipped (duplicate) for device [{}] at [{}]", deviceId, result.getCalculatedTime());
        }
        
        // 5. 推送结果
        telemetryService.pushResult(deviceId, calcResult);
        
        return result;
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
}
