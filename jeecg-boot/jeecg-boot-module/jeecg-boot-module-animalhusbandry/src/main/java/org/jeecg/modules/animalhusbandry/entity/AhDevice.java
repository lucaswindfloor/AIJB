package org.jeecg.modules.animalhusbandry.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @Description: 设备信息表
 * @Author: jeecg-boot
 * @Date:   2024-08-26
 * @Version: V1.0
 */
@Data
@TableName("ah_device")
@Schema(name="ah_device对象", description="设备信息表")
public class AhDevice implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
	private java.lang.String id;
	/**设备名称 (别名, 优先使用用户在本平台的命名)*/
	@Excel(name = "设备名称", width = 15)
    @Schema(description = "设备名称 (别名, 优先使用用户在本平台的命名)")
	private java.lang.String name;
	/**设备类型 (字典: device_type, CAPSULE, TRACKER)*/
	@Excel(name = "设备类型", width = 15, dicCode = "device_type")
	@Dict(dicCode = "device_type")
    @Schema(description = "设备类型 (字典: device_type, CAPSULE, TRACKER)")
	private java.lang.String deviceType;
	/**LoRaWAN DevEUI (唯一)*/
	@Excel(name = "DevEUI", width = 15)
    @Schema(description = "LoRaWAN DevEUI (唯一)")
	private java.lang.String devEui;
	/**ThingsBoard平台设备ID (唯一, 同步时写入)*/
	@Excel(name = "ThingsBoard ID", width = 15)
    @Schema(description = "ThingsBoard平台设备ID (唯一, 同步时写入)")
	private java.lang.String tbDeviceId;
	/**设备型号*/
	@Excel(name = "设备型号", width = 15)
    @Schema(description = "设备型号")
	private java.lang.String model;
	/**采购日期*/
	@Excel(name = "采购日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "采购日期")
	private java.util.Date purchaseDate;
	/**设备生命周期状态 (字典: device_lifecycle_status)*/
	@Excel(name = "生命周期状态", width = 15, dicCode = "device_lifecycle_status")
	@Dict(dicCode = "device_lifecycle_status")
    @Schema(description = "设备生命周期状态 (字典: device_lifecycle_status)")
	private java.lang.String status;
	/**最后一次的信号强度RSSI*/
	@Excel(name = "RSSI", width = 15)
    @Schema(description = "最后一次的信号强度RSSI")
	private java.lang.Integer rssi;
    /**最后一次的信噪比SNR*/
    @Excel(name = "SNR", width = 15)
    @Schema(description = "最后一次的信噪比SNR")
    private java.math.BigDecimal loRaSnr;
	/**硬件版本*/
	@Excel(name = "硬件版本", width = 15)
	@Schema(description = "硬件版本")
	private java.lang.String hardwareVersion;
	/**工作模式*/
	@Excel(name = "工作模式", width = 15)
	@Schema(description = "工作模式")
	private java.lang.String workMode;
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
} 