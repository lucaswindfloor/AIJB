package org.jeecg.modules.moldai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.moldai.entity.MoldAiRiskResult;
import org.jeecg.modules.moldai.service.IMoldAiAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 霉菌风险分析接口
 */
@Slf4j
@RestController
@RequestMapping("/mold_ai/analysis")
@Tag(name = "霉菌风险分析", description = "霉菌风险预测AI分析接口")
public class MoldAiAnalysisController {
    
    @Autowired
    private IMoldAiAnalysisService analysisService;
    
    /**
     * 计算设备风险（核心接口）
     */
    @GetMapping("/analyze/{deviceId}")
    @Operation(summary = "计算设备霉菌风险")
    public Result<MoldAiRiskResult> analyze(@PathVariable String deviceId) {
        MoldAiRiskResult result = analysisService.analyze(deviceId);
        return Result.OK(result);
    }
    
    /**
     * 获取最新结果
     */
    @GetMapping("/result/{deviceId}")
    @Operation(summary = "获取最新风险结果")
    public Result<MoldAiRiskResult> getLatest(@PathVariable String deviceId) {
        MoldAiRiskResult result = analysisService.getLatest(deviceId);
        return Result.OK(result);
    }
}


