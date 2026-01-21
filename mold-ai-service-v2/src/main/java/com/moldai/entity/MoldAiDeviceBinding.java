package com.moldai.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("mai_device_binding")
public class MoldAiDeviceBinding implements Serializable {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    private String deviceId;
    
    private String sceneId;
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}

