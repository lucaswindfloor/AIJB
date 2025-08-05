package org.jeecg.modules.animalhusbandry.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
@Schema(name = "AhDeviceUnbindDTO", description = "设备解绑操作数据传输对象")
public class AhDeviceUnbindDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "设备ID不能为空")
    @Schema(description = "要解绑的设备ID", required = true)
    private String deviceId;
} 