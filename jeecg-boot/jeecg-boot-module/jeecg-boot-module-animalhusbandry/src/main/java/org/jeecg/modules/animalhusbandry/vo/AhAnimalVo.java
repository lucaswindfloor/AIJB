package org.jeecg.modules.animalhusbandry.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

@Data
@Schema(name = "AhAnimalVo", description = "用于设备列表展示的牲畜信息")
public class AhAnimalVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "牲畜耳标号")
    private String earTagId;

    @Schema(description = "牲畜名称")
    private String name;

    @Schema(description = "本系统牲畜ID")
    private String animalId;
} 