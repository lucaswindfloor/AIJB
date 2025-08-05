package org.jeecg.modules.animalhusbandry.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.modules.animalhusbandry.entity.AhDevice;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "AhDevicePage", description = "设备台账分页查询返回对象")
public class AhDevicePage extends AhDevice {
    private static final long serialVersionUID = 1L;

    @Schema(description = "当前绑定的牲畜信息 (如果已绑定)")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private AhAnimalVo boundAnimalInfo;
} 