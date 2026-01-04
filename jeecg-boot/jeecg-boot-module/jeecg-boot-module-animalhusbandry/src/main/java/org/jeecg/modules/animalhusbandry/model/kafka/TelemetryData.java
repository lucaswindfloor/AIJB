package org.jeecg.modules.animalhusbandry.model.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 对应遥测数据 (data字段) 的内部结构
 */
@Data
public class TelemetryData {

    @JsonProperty("Temperature")
    private Double temperature;

    @JsonProperty("Gastric_momentum")
    private Long gastricMomentum;

    // 您提供的JSON中还有Temperature_1到_6，如果需要存储，可以在这里添加
    // private Double temperature1;
    // ...
}