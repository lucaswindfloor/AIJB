package com.moldai.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 联动设备（执行器）实体
 * 如：车位锁、LoRaWAN开关、排风扇、除湿机、加热器等
 */
@Data
@TableName("mai_actuator")
public class MoldAiActuator implements Serializable {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 设备ID（ThingsBoard平台设备标识）
     */
    private String deviceId;
    
    /**
     * 设备名称
     */
    private String deviceName;
    
    /**
     * 设备类型：parking_lock/lorawan_switch/exhaust_fan/dehumidifier/heater
     */
    private String deviceType;
    
    /**
     * RPC调用方法名
     */
    private String rpcMethod;
    
    /**
     * RPC默认参数(JSON格式)
     */
    private String rpcParams;
    
    /**
     * 是否启用：1-是，0-否
     */
    private Integer enabled;
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}

