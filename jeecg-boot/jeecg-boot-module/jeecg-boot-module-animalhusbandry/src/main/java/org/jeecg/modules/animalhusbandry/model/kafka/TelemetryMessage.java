package org.jeecg.modules.animalhusbandry.model.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.Map;

/**
 * 对应从Kafka接收到的最外层JSON结构
 */
@Data
public class TelemetryMessage {

    /**
     * 设备名称/ID
     */
    private String deviceName;

    /**
     * 时间戳
     */
    private String ts;

    /**
     * 遥测数据
     */
    private TelemetryData data;

    /**
     * LoRaWAN信号强度
     */
    private Double loRaSNR;

    /**
     * LoRaWAN接收信号强度指示
     */
    private Integer rssi;
}