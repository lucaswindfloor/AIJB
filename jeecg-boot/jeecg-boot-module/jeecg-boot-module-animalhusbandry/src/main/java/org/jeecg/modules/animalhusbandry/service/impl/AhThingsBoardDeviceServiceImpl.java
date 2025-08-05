package org.jeecg.modules.animalhusbandry.service.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.animalhusbandry.service.IAhThingsBoardDeviceService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service("ahThingsBoardDeviceServiceImpl") // 使用明确的、带前缀的Bean名称
@Slf4j
public class AhThingsBoardDeviceServiceImpl implements IAhThingsBoardDeviceService {

    /**
     * 模拟根据ThingsBoard设备ID获取最新遥测数据
     * @param tbDeviceId ThingsBoard设备ID
     * @return 遥测数据
     */
    @Override
    public Result<?> getLatestTelemetry(String tbDeviceId) {
        log.info("【畜牧业模块】正在为设备ID: {} 获取最新遥测数据 (模拟)", tbDeviceId);

        // 在实际应用中, 这里会使用HTTP客户端调用ThingsBoard的API
        // GET /api/plugins/telemetry/DEVICE/{deviceId}/values/timeseries
        // 为了演示，我们返回一个模拟的JSON对象
        JSONObject telemetryData = new JSONObject();
        telemetryData.put("ts", System.currentTimeMillis());

        // 模拟不同设备类型返回不同数据
        // 动物追踪器
        telemetryData.put("latitude", "39.9042");
        telemetryData.put("longitude", "116.4074");
        telemetryData.put("step", 2345);
        telemetryData.put("battery", 98);

        // 瘤胃胶囊
        telemetryData.put("Temperature", new BigDecimal("38.5"));
        telemetryData.put("Gastric_momentum", 814791);
        telemetryData.put("rssi", -92);
        telemetryData.put("loRaSNR", new BigDecimal("-6.0"));

        return Result.OK(telemetryData);
    }
} 