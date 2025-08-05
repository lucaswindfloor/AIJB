package org.jeecg.modules.animalhusbandry.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

/**
 * @Description: 遥测历史数据查询DTO
 * @Author: Gemini
 * @Date:   2024-08-23
 * @Version: V1.0
 */
@Schema(description = "遥测历史数据查询DTO")
public class TelemetryHistoryQueryDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "目标设备ID", required = true)
    private String deviceId;

    @Schema(description = "要查询的遥测键名，多个用逗号分隔 (例如: battery,rssi,snr)", required = true)
    private String keys;

    @Schema(description = "查询起始时间戳 (毫秒)", required = true)
    private Long startTs;

    @Schema(description = "查询结束时间戳 (毫秒)", required = true)
    private Long endTs;

    // --- Manual getters and setters ---

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getKeys() {
        return keys;
    }

    public void setKeys(String keys) {
        this.keys = keys;
    }

    public Long getStartTs() {
        return startTs;
    }

    public void setStartTs(Long startTs) {
        this.startTs = startTs;
    }

    public Long getEndTs() {
        return endTs;
    }

    public void setEndTs(Long endTs) {
        this.endTs = endTs;
    }
} 