package org.jeecg.modules.parking.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;

/**
 * @Description: 车位锁设备
 * @Author: jeecg-boot
 * @Date:   2025-07-01
 * @Version: V1.0
 */
@Data
@TableName("p_lock_device")
@Schema(name="p_lock_device", description="车位锁设备")
public class LockDevice implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
	private java.lang.String id;
	/**创建人*/
    @Schema(description = "创建人")
	private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建日期")
	private java.util.Date createTime;
	/**更新人*/
    @Schema(description = "更新人")
	private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新日期")
	private java.util.Date updateTime;
	/**设备编号*/
	@Excel(name = "设备编号", width = 15)
    @Schema(description = "设备编号")
	private java.lang.String deviceNo;
	/**设备名称*/
	@Excel(name = "设备名称", width = 15)
    @Schema(description = "设备名称")
	private java.lang.String deviceName;
	/**所属停车场ID*/
	@Excel(name = "所属停车场", width = 15, dictTable = "p_parking_lot", dicCode = "id", dicText = "name")
    @Schema(description = "所属停车场ID")
    @Dict(dictTable = "p_parking_lot", dicCode = "id", dicText = "name")
	private java.lang.String parkingLotId;
	/**绑定车位ID*/
	@Excel(name = "绑定车位", width = 15)
    @Schema(description = "绑定车位ID")
	private java.lang.String parkingSpaceId;
	/**锁状态;0-降下，1-升起*/
	@Excel(name = "锁状态", width = 15, dicCode = "lock_status")
    @Schema(description = "锁状态")
    @Dict(dicCode = "lock_status")
	private java.lang.Integer lockStatus;
	/**占用状态;0-空闲, 1-占用*/
	@Excel(name = "占用状态", width = 15, dicCode = "occupied_status")
    @Schema(description = "占用状态")
    @Dict(dicCode = "occupied_status")
	private java.lang.Integer isOccupied;
	/**电池电量*/
	@Excel(name = "电池电量", width = 15)
    @Schema(description = "电池电量")
	private java.lang.Integer batteryLevel;
	/**信号强度*/
	@Excel(name = "信号强度", width = 15)
    @Schema(description = "信号强度")
	private java.lang.Integer signalStrength;
	/**设备状态;0-停用,1-正常,2-故障*/
	@Excel(name = "设备状态", width = 15, dicCode = "device_status")
    @Schema(description = "设备状态")
    @Dict(dicCode = "device_status")
	private java.lang.String status;
	/**最后心跳时间*/
	@Excel(name = "最后心跳时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "最后心跳时间")
	private java.util.Date lastHeartbeatTime;
	/**LoRaWAN DevEUI*/
	@Excel(name = "DevEUI", width = 15)
    @Schema(description = "LoRaWAN DevEUI")
	private java.lang.String devEui;
	/**LoRaWAN AppEUI*/
	@Excel(name = "AppEUI", width = 15)
    @Schema(description = "LoRaWAN AppEUI")
	private java.lang.String appEui;
	/**LoRaWAN AppKey*/
	@Excel(name = "AppKey", width = 15)
    @Schema(description = "LoRaWAN AppKey")
	private java.lang.String appKey;
    /**ThingsBoard设备ID*/
	@Excel(name = "ThingsBoard设备ID", width = 15)
    @Schema(description = "ThingsBoard设备ID")
	private java.lang.String tbDeviceId;
} 