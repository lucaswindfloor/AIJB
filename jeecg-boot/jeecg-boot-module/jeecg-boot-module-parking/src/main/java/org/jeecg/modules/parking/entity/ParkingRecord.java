package org.jeecg.modules.parking.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @Description: 停车记录表
 * @Author: jeecg-boot
 * @Date:   2024-08-12
 * @Version: V1.0
 */
@Schema(description="停车记录表")
@Data
@TableName("p_parking_record")
public class ParkingRecord implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private String id;
	/**停车场ID*/
	@Excel(name = "停车场ID", width = 15)
    @Schema(description = "停车场ID")
    private String parkingLotId;
	/**车辆ID*/
	@Excel(name = "车辆ID", width = 15)
    @Schema(description = "车辆ID")
    private String vehicleId;
	/**车牌号*/
	@Excel(name = "车牌号", width = 15)
    @Schema(description = "车牌号")
    private String licensePlate;
	/**车主姓名*/
	@Excel(name = "车主姓名", width = 15)
    @Schema(description = "车主姓名")
    private String ownerName;
	/**车辆类型*/
	@Excel(name = "车辆类型", width = 15)
    @Schema(description = "车辆类型")
    private String vehicleType;
	/**入场时间*/
	@Excel(name = "入场时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "入场时间")
    private Date entryTime;
	/**入口关卡ID*/
	@Excel(name = "入口关卡ID", width = 15)
    @Schema(description = "入口关卡ID")
    private String entryCheckpointId;
	/**入场放行方式*/
	@Excel(name = "入场放行方式", width = 15)
    @Schema(description = "入场放行方式")
    private String entryReleaseType;
	/**入场抓拍图片URL*/
	@Excel(name = "入场抓拍图片URL", width = 15)
    @Schema(description = "入场抓拍图片URL")
    private String entryImageUrl;
	/**出场时间*/
	@Excel(name = "出场时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "出场时间")
    private Date exitTime;
	/**出口关卡ID*/
	@Excel(name = "出口关卡ID", width = 15)
    @Schema(description = "出口关卡ID")
    private String exitCheckpointId;
	/**出场放行方式*/
	@Excel(name = "出场放行方式", width = 15)
    @Schema(description = "出场放行方式")
    private String exitReleaseType;
	/**出场抓拍图片URL*/
	@Excel(name = "出场抓拍图片URL", width = 15)
    @Schema(description = "出场抓拍图片URL")
    private String exitImageUrl;
	/**停放的车位编号*/
	@Excel(name = "停放的车位编号", width = 15)
    @Schema(description = "停放的车位编号")
    private String spaceNo;
	/**状态*/
	@Excel(name = "状态", width = 15)
    @Schema(description = "状态")
    private String status;
	/**应收金额*/
	@Excel(name = "应收金额", width = 15)
    @Schema(description = "应收金额")
    private BigDecimal payableAmount;
	/**实收金额*/
	@Excel(name = "实收金额", width = 15)
    @Schema(description = "实收金额")
    private BigDecimal actualAmount;
	/**优惠金额*/
	@Excel(name = "优惠金额", width = 15)
    @Schema(description = "优惠金额")
    private BigDecimal discountAmount;
	/**支付时间*/
	@Excel(name = "支付时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "支付时间")
    private Date payTime;
	/**支付方式*/
	@Excel(name = "支付方式", width = 15)
    @Schema(description = "支付方式")
    private String paymentMethod;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private String remarks;
	/**创建人*/
    @Schema(description = "创建人")
    private String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private Date createTime;
	/**更新人*/
    @Schema(description = "更新人")
    private String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private Date updateTime;
} 