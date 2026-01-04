package org.jeecg.modules.animalhusbandry.vo;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @Description: 设备监控KPI视图对象
 * @Author: Gemini
 * @Date: 2024-08-23
 * @Version: V1.0
 */
@Schema(description = "设备监控KPI视图对象")
public class DeviceKpiVo {

    @Schema(description = "设备总数")
    private Long total = 0L;

    @Schema(description = "在线设备数")
    private Long online = 0L;

    @Schema(description = "离线设备数")
    private Long offline = 0L;

    @Schema(description = "低电量设备数")
    private Long lowBattery = 0L;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getOnline() {
        return online;
    }

    public void setOnline(Long online) {
        this.online = online;
    }

    public Long getOffline() {
        return offline;
    }

    public void setOffline(Long offline) {
        this.offline = offline;
    }

    public Long getLowBattery() {
        return lowBattery;
    }

    public void setLowBattery(Long lowBattery) {
        this.lowBattery = lowBattery;
    }
} 