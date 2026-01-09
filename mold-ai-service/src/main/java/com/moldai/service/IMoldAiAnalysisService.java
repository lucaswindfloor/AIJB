package com.moldai.service;

import com.moldai.entity.MoldAiRiskResult;

public interface IMoldAiAnalysisService {
    
    /**
     * 对指定设备进行霉菌风险分析
     * @param deviceId 设备ID
     * @return 分析结果
     */
    MoldAiRiskResult analyze(String deviceId);
    
    /**
     * 获取设备最新的风险分析结果
     * @param deviceId 设备ID
     * @return 最新结果
     */
    MoldAiRiskResult getLatest(String deviceId);
}


