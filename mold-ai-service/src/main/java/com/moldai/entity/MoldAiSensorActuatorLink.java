package com.moldai.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 传感器-联动设备关联实体
 * 用于记录哪个传感器在高风险时触发哪些联动设备
 */
@Data
@TableName("mai_sensor_actuator_link")
public class MoldAiSensorActuatorLink implements Serializable {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 传感器设备ID
     */
    private String sensorDeviceId;
    
    /**
     * 联动设备ID（mai_actuator表的id）
     */
    private String actuatorId;
    
    /**
     * 触发风险等级：HIGH/MEDIUM/LOW
     */
    private String triggerLevel;
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}


