package org.jeecg.modules.animalhusbandry.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.modules.animalhusbandry.entity.AhAnimal;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "AhAnimalPageVO", description = "牲畜档案分页查询返回对象（多列方案）")
public class AhAnimalPageVO extends AhAnimal {
    private static final long serialVersionUID = 1L;

    @Schema(description = "绑定的设备Map，Key为设备类型 (CAPSULE, TRACKER), Value为设备信息")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, AhDeviceVo> deviceMap;
} 