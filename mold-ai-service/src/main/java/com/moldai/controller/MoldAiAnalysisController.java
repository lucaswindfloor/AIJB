package com.moldai.controller;

import com.moldai.entity.MoldAiRiskResult;
import com.moldai.entity.MoldAiScene;
import com.moldai.service.IMoldAiAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mold-ai")
@CrossOrigin(origins = "*") // 允许跨域，方便前端调试
public class MoldAiAnalysisController {

    @Autowired
    private IMoldAiAnalysisService analysisService;

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
}
