package org.jeecg.modules.animalhusbandry.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(name = "AhDeviceVo", description = "用于牲畜列表展示的简洁设备信息")
public class AhDeviceVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "设备ID")
    private String id;

    @Schema(description = "设备名称")
    private String name;

    @Schema(description = "设备类型")
    private String deviceType;

    @Schema(description = "关联的牲畜ID (用于分组)")
    private String animalId;
} 