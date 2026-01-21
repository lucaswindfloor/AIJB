package com.moldai.service;

import com.moldai.algorithm.VTTCalculator;
import java.util.List;

public interface ITelemetryService {
    
    /**
     * 查询历史遥测数据
     * @param deviceId 设备ID
     * @param startTs 开始时间戳
     * @param endTs 结束时间戳
     * @param interval 聚合间隔 (e.g. "1h")
     */
    List<VTTCalculator.DataPoint> queryTelemetry(String deviceId, long startTs, long endTs, String interval);
    
    /**
     * 推送分析结果
     */
    void pushResult(String deviceId, VTTCalculator.RiskResult result);
}


