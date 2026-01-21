package com.moldai.service;

import com.moldai.entity.MoldAiRiskResult;
import com.moldai.entity.MoldAiScene;

import java.util.List;

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

    /**
     * 获取所有可用场景
     * @return 场景列表
     */
    List<MoldAiScene> getSceneList();

    /**
     * 获取设备的历史分析记录
     * @param deviceId 设备ID
     * @param limit 条数限制
     * @return 历史记录列表
     */
    List<MoldAiRiskResult> getHistory(String deviceId, int limit);
}
