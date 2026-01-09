package org.jeecg.modules.moldai.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("mold_ai_device_binding")
@Schema(description = "设备场景绑定")
public class MoldAiDeviceBinding implements Serializable {
    
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private String id;
    
    @Schema(description = "设备ID")
    private String deviceId;
    
    @Schema(description = "场景ID")
    private String sceneId;
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}


