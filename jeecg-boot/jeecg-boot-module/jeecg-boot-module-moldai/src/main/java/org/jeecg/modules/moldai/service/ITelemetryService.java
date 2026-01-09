package org.jeecg.modules.moldai.service;

import org.jeecg.modules.moldai.algorithm.VTTCalculator;

import java.util.List;

/**
 * 遥测数据服务接口
 * 支持多种数据源实现（TDengine, ThingsBoard API, MySQL等）
 */
public interface ITelemetryService {

    /**
     * 查询指定设备在一段时间内的温湿度数据（支持聚合）
     *
     * @param deviceId 设备ID
     * @param startTs  开始时间戳
     * @param endTs    结束时间戳
     * @param interval 聚合间隔 (null表示不聚合, "1h"表示1小时)
     * @return 温湿度数据点列表
     */
    List<VTTCalculator.DataPoint> queryTelemetry(String deviceId, long startTs, long endTs, String interval);

    /**
     * 推送分析结果
     *
     * @param deviceId 设备ID
     * @param result   风险计算结果
     */
    void pushResult(String deviceId, VTTCalculator.RiskResult result);
}
