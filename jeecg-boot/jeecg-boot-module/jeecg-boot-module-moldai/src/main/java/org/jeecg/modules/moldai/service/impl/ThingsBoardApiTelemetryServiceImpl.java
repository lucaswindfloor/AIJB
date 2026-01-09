package org.jeecg.modules.moldai.service.impl;

import org.jeecg.modules.moldai.algorithm.VTTCalculator;
import org.jeecg.modules.moldai.platform.ThingsBoardClient;
import org.jeecg.modules.moldai.service.ITelemetryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("thingsboardTelemetryService")
@ConditionalOnProperty(name = "mold-ai.platform.type", havingValue = "thingsboard")
public class ThingsBoardApiTelemetryServiceImpl implements ITelemetryService {

    @Autowired
    private ThingsBoardClient thingsBoardClient;

    @Override
    public List<VTTCalculator.DataPoint> queryTelemetry(String deviceId, long startTs, long endTs, String interval) {
        // ThingsBoard API 默认返回的是原始数据，如果需要聚合，也需要在这里做
        // 暂时直接透传，未来可以在 ThingsBoardClient 中实现聚合参数透传
        // TB API 支持 &interval=3600000&agg=AVG
        return thingsBoardClient.queryTelemetry(deviceId, startTs, endTs);
    }

    @Override
    public void pushResult(String deviceId, VTTCalculator.RiskResult result) {
        thingsBoardClient.pushResult(deviceId, result);
    }
}
