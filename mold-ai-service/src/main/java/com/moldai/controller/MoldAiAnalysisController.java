package com.moldai.controller;

import com.moldai.entity.MoldAiRiskResult;
import com.moldai.service.IMoldAiAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mold-ai")
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
}


