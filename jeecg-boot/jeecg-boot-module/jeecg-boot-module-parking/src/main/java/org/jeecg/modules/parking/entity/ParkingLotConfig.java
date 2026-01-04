package org.jeecg.modules.parking.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description="停车场业务配置表")
@Data
@TableName("p_parking_lot_config")
public class ParkingLotConfig implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private java.lang.String id;
	/**停车场ID*/
	@Excel(name = "停车场ID", width = 15)
    @Schema(description = "停车场ID")
    private java.lang.String parkingLotId;
	/**免费停车时长(分钟)*/
	@Excel(name = "免费停车时长(分钟)", width = 15)
    @Schema(description = "免费停车时长(分钟)")
    private java.lang.Integer freeDuration;
	/**过期月租车免费时长(分钟)*/
	@Excel(name = "过期月租车免费时长(分钟)", width = 15)
    @Schema(description = "过期月租车免费时长(分钟)")
    private java.lang.Integer expiredCarFreeDuration;
	/**计费模式*/
	@Excel(name = "计费模式", width = 15)
    @Schema(description = "计费模式")
    private java.lang.String billingModel;
	/**起步价*/
	@Excel(name = "起步价", width = 15)
    @Schema(description = "起步价")
    private java.math.BigDecimal startingPrice;
	/**允许无牌车入场*/
	@Excel(name = "允许无牌车入场", width = 15, dicCode = "yn")
    @Schema(description = "允许无牌车入场")
    private java.lang.Integer allowUnlicensedEntry;
	/**禁止同组车辆同时在场*/
	@Excel(name = "禁止同组车辆同时在场", width = 15, dicCode = "yn")
    @Schema(description = "禁止同组车辆同时在场")
    private java.lang.Integer forbidSimultaneousEntry;
	/**车位已满时是否限制入场*/
	@Excel(name = "车位已满时是否限制入场", width = 15, dicCode = "yn")
    @Schema(description = "车位已满时是否限制入场")
    private java.lang.Integer limitOnFull;
	/**自动续费月卡*/
	@Excel(name = "自动续费月卡", width = 15, dicCode = "yn")
    @Schema(description = "自动续费月卡")
    private java.lang.Integer autoRenewSubscription;
	/**临时车是否自动支付*/
	@Excel(name = "临时车是否自动支付", width = 15, dicCode = "yn")
    @Schema(description = "临时车是否自动支付")
    private java.lang.Integer tempCarAutoPay;
	/**异常记录是否自动放行*/
	@Excel(name = "异常记录是否自动放行", width = 15, dicCode = "yn")
    @Schema(description = "异常记录是否自动放行")
    private java.lang.Integer abnormalAutoRelease;
	/**允许收费员修改停车记录*/
	@Excel(name = "允许收费员修改停车记录", width = 15, dicCode = "yn")
    @Schema(description = "允许收费员修改停车记录")
    private java.lang.Integer allowEditRecord;
	/**套餐车是否自动下发白名单*/
	@Excel(name = "套餐车是否自动下发白名单", width = 15, dicCode = "yn")
    @Schema(description = "套餐车是否自动下发白名单")
    private java.lang.Integer autoIssueWhitelist;
	/**是否显示停车记录备注*/
	@Excel(name = "是否显示停车记录备注", width = 15, dicCode = "yn")
    @Schema(description = "是否显示停车记录备注")
    private java.lang.Integer showRecordRemarks;
	/**访客审核模式*/
	@Excel(name = "访客审核模式", width = 15)
    @Schema(description = "访客审核模式")
    private java.lang.String visitorApprovalMode;
	/**费用代收停车场ID*/
	@Excel(name = "费用代收停车场ID", width = 15)
    @Schema(description = "费用代收停车场ID")
    private java.lang.String paymentCollectionLotId;
	/**数据上报平台*/
	@Excel(name = "数据上报平台", width = 15)
    @Schema(description = "数据上报平台")
    private java.lang.String dataReportingPlatform;
	/**创建人ID*/
    @Schema(description = "创建人ID")
    private java.lang.String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private java.util.Date createTime;
	/**更新人ID*/
    @Schema(description = "更新人ID")
    private java.lang.String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private java.util.Date updateTime;
} 