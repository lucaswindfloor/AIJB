package org.jeecg.modules.parking.entity;

import java.io.Serializable;
import java.math.BigDecimal;
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

/**
 * @Description: 车辆信息表
 * @Author: jeecg-boot
 * @Date:   2024-08-12
 * @Version: V1.1
 */
@Data
@TableName("p_vehicle")
@Schema(description="车辆信息表")
public class PVehicle implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
	private java.lang.String id;
	/**创建人*/
    @Schema(description = "创建人")
	private java.lang.String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
	private java.util.Date createTime;
	/**更新人*/
    @Schema(description = "更新人")
	private java.lang.String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
	private java.util.Date updateTime;
	/**组织机构代码*/
    @Schema(description = "组织机构代码")
	private java.lang.String sysOrgCode;
	/**车牌号*/
	@Excel(name = "车牌号", width = 15)
    @Schema(description = "车牌号")
	private java.lang.String licensePlate;
	/**车主用户ID*/
	@Excel(name = "车主用户ID", width = 15)
    @Schema(description = "车主用户ID")
	private java.lang.String ownerId;
	/**车主姓名*/
	@Excel(name = "车主姓名", width = 15)
    @Schema(description = "车主姓名")
	private java.lang.String ownerName;
	/**车主手机号*/
	@Excel(name = "车主手机号", width = 15)
    @Schema(description = "车主手机号")
	private java.lang.String ownerPhone;
	/**所属订阅组ID*/
	@Excel(name = "所属订阅组ID", width = 15)
    @Schema(description = "所属订阅组ID")
	private java.lang.String groupId;
	/**套餐剩余次数/数量*/
	@Excel(name = "套餐剩余次数/数量", width = 15)
	@Schema(description = "套餐剩余次数/数量")
	private java.lang.Integer packageCounts;
	/**车辆图片*/
	@Excel(name = "车辆图片", width = 15)
    @Schema(description = "车辆图片")
	private java.lang.String image;
	/**汽车品牌*/
	@Excel(name = "汽车品牌", width = 15)
    @Schema(description = "汽车品牌")
	private java.lang.String brand;
	/**型号*/
	@Excel(name = "型号", width = 15)
    @Schema(description = "型号")
	private java.lang.String model;
	/**购买时间*/
	@Excel(name = "购买时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "购买时间")
	private java.util.Date purchaseTime;
	/**购买价格*/
	@Excel(name = "购买价格", width = 15)
    @Schema(description = "购买价格")
	private java.math.BigDecimal price;
	/**状态(0-禁用, 1-审核中, 2-正常)*/
	@Excel(name = "状态(0-禁用, 1-审核中, 2-正常)", width = 15)
    @Schema(description = "状态(0-禁用, 1-审核中, 2-正常)")
	private java.lang.String status;
	/**证件照*/
	@Excel(name = "证件照", width = 15)
    @Schema(description = "证件照")
	private java.lang.String identityImage;
	/**证件号*/
	@Excel(name = "证件号", width = 15)
    @Schema(description = "证件号")
	private java.lang.String identityNumber;
	/**车辆类型*/
	@Excel(name = "车辆类型", width = 15)
    @Schema(description = "车辆类型")
	private java.lang.String vehicleType;
	/**类型有效期开始时间*/
	@Excel(name = "类型有效期开始时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "类型有效期开始时间")
	private java.util.Date vehicleTypeStartTime;
	/**类型有效期结束时间*/
	@Excel(name = "类型有效期结束时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "类型有效期结束时间")
	private java.util.Date vehicleTypeEndTime;
} 