package org.jeecg.modules.animalhusbandry.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用于封装地图上显示的单个牲畜位置和状态的VO")
public class MapAnimalDataVo {

    @Schema(description = "牲畜ID")
    private String animalId;

    @Schema(description = "耳标号")
    private String earTagId;

    @Schema(description = "经度")
    private Double lon;

    @Schema(description = "纬度")
    private Double lat;

    @Schema(description = "健康状态 (HEALTHY, SUB_HEALTHY, ALARM)")
    private String healthStatus;
} 