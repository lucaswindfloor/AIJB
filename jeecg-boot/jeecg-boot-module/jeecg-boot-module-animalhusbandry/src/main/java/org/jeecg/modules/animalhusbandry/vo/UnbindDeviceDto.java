package org.jeecg.modules.animalhusbandry.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "解绑设备DTO", description = "用于封装设备解绑的请求参数")
public class UnbindDeviceDto {

    @Schema(description = "设备ID", required = true)
    private String deviceId;

} 