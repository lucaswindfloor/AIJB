package org.jeecg.modules.animalhusbandry.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name="设备绑定DTO", description="从前端接收用于绑定的设备ID和牲畜ID")
public class AhDeviceBindDTO {

    @Schema(description = "要绑定的设备ID", required = true)
    private String deviceId;

    @Schema(description = "要绑定的牲畜ID", required = true)
    private String animalId;
} 