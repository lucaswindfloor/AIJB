package com.moldai.controller;

import com.moldai.algorithm.VTTCalculator;
import com.moldai.entity.MoldAiRiskResult;
import com.moldai.entity.MoldAiScene;
import com.moldai.service.IMoldAiAnalysisService;
import com.moldai.service.ITelemetryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/api/v1/mold-ai")
@CrossOrigin(origins = "*") // 允许跨域，方便前端调试
public class MoldAiAnalysisController {

    @Autowired
    private IMoldAiAnalysisService analysisService;
    
    @Autowired
    private ITelemetryService telemetryService;
    
    @PostMapping("/analyze/{deviceId}")
    public ApiResponse<MoldAiRiskResult> analyze(@PathVariable String deviceId) {
        try {
            MoldAiRiskResult result = analysisService.analyze(deviceId);
            if (result == null) {
                return ApiResponse.fail("Analysis failed or skipped (check logs)");
            }
            return ApiResponse.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fail(e.getMessage());
        }
    }

    @GetMapping("/result/latest/{deviceId}")
    public ApiResponse<MoldAiRiskResult> getLatest(@PathVariable String deviceId) {
        return ApiResponse.success(analysisService.getLatest(deviceId));
    }

    @GetMapping("/result/history/{deviceId}")
    public ApiResponse<List<MoldAiRiskResult>> getHistory(@PathVariable String deviceId, 
                                                          @RequestParam(defaultValue = "20") int limit) {
        return ApiResponse.success(analysisService.getHistory(deviceId, limit));
    }

    @GetMapping("/scene/list")
    public ApiResponse<List<MoldAiScene>> getSceneList() {
        return ApiResponse.success(analysisService.getSceneList());
    }
    
    /**
     * 获取设备的温湿度历史数据
     * @param deviceId 设备ID
     * @param days 查询天数（默认7天）
     * @param interval 聚合间隔：30m（30分钟）、1h（1小时）、15m（15分钟），默认30m
     */
    @GetMapping("/telemetry/history/{deviceId}")
    public ApiResponse<List<VTTCalculator.DataPoint>> getTelemetryHistory(
            @PathVariable String deviceId,
            @RequestParam(defaultValue = "7") int days,
            @RequestParam(defaultValue = "30m") String interval) {
        try {
            Instant endTime = Instant.now();
            Instant startTime = endTime.minus(days, ChronoUnit.DAYS);
            
            List<VTTCalculator.DataPoint> dataPoints = telemetryService.queryTelemetry(
                deviceId, startTime.toEpochMilli(), endTime.toEpochMilli(), interval);
            
            return ApiResponse.success(dataPoints);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.fail("Failed to fetch telemetry: " + e.getMessage());
        }
    }
}
