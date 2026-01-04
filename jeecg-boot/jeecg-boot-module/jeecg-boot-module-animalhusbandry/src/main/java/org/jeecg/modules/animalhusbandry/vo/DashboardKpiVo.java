package org.jeecg.modules.animalhusbandry.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "封装驾驶舱顶部核心KPI数据的VO")
public class DashboardKpiVo {

    @Schema(description = "牲畜总数")
    private int totalAnimals;

    @Schema(description = "设备总数")
    private int totalDevices;

    @Schema(description = "设备在线数")
    private int onlineDevices;

    @Schema(description = "健康数量")
    private Integer healthyCount = 0;

    @Schema(description = "亚健康数量")
    private Integer subHealthyCount = 0;

    @Schema(description = "告警数量")
    private Integer alarmCount = 0;

    @Schema(description = "今日新增告警")
    private Integer newAlarmsToday = 0;
} 