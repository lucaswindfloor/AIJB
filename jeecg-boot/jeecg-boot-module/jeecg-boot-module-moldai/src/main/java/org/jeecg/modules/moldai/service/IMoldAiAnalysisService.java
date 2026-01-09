package org.jeecg.modules.moldai.service;

import org.jeecg.modules.moldai.entity.MoldAiRiskResult;

public interface IMoldAiAnalysisService {
    /**
     * 计算设备风险
     */
    MoldAiRiskResult analyze(String deviceId);
    
    /**
     * 获取最新结果
     */
    MoldAiRiskResult getLatest(String deviceId);
}


