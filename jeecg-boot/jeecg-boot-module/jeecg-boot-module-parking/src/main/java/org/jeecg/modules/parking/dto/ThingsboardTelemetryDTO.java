package org.jeecg.modules.parking.dto;

import lombok.Data;
import lombok.ToString;

import java.util.Map;

/**
 * 用于接收 ThingsBoard Webhook 遥测数据的 DTO
 */
@Data
@ToString
public class ThingsboardTelemetryDTO {

    /**
     * 设备编号 (对应 ThingsBoard 中的设备名称)
     */
    private String deviceNo;

    /**
     * 遥测数据
     * e.g. {"lockStatus": 1, "batteryLevel": 85}
     */
    private Map<String, Object> data;
} 