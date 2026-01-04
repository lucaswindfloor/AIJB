package org.jeecg.modules.animalhusbandry.vo;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(name = "绑定设备DTO", description = "用于封装设备绑定到牲畜的请求参数")
public class BindDeviceDto {

    @Schema(description = "牲畜ID", required = true)
    private String animalId;

    @Schema(description = "设备ID", required = true)
    private String deviceId;
} 