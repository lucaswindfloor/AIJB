package org.jeecg.modules.parking.entity;

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

@Schema(description="停车场核心信息表")
@Data
@TableName("p_parking_lot")
public class ParkingLot implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private java.lang.String id;
	/**停车场名称*/
	@Excel(name = "停车场名称", width = 15)
    @Schema(description = "停车场名称")
    private java.lang.String name;
	/**停车场封面图*/
	@Excel(name = "停车场封面图", width = 15)
    @Schema(description = "停车场封面图")
    private java.lang.String image;
	/**详细地址*/
	@Excel(name = "详细地址", width = 15)
    @Schema(description = "详细地址")
    private java.lang.String address;
	/**经度*/
	@Excel(name = "经度", width = 15)
    @Schema(description = "经度")
    private java.math.BigDecimal longitude;
	/**纬度*/
	@Excel(name = "纬度", width = 15)
    @Schema(description = "纬度")
    private java.math.BigDecimal latitude;
	/**省份ID*/
	@Excel(name = "省份ID", width = 15)
    @Schema(description = "省份ID")
    private java.lang.String provinceId;
	/**城市ID*/
	@Excel(name = "城市ID", width = 15)
    @Schema(description = "城市ID")
    private java.lang.String cityId;
	/**区/县ID*/
	@Excel(name = "区/县ID", width = 15)
    @Schema(description = "区/县ID")
    private java.lang.String areaId;
	/**车位总数*/
	@Excel(name = "车位总数", width = 15)
    @Schema(description = "车位总数")
    private java.lang.Integer totalSpaces;
	/**联系人*/
	@Excel(name = "联系人", width = 15)
    @Schema(description = "联系人")
    private java.lang.String contactPerson;
	/**联系电话*/
	@Excel(name = "联系电话", width = 15)
    @Schema(description = "联系电话")
    private java.lang.String contactPhone;
	/**营业开始时间*/
	@Excel(name = "营业开始时间", width = 15, format = "HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "HH:mm:ss")
    @DateTimeFormat(pattern="HH:mm:ss")
    @Schema(description = "营业开始时间")
    private java.util.Date businessHoursStart;
	/**营业结束时间*/
	@Excel(name = "营业结束时间", width = 15, format = "HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "HH:mm:ss")
    @DateTimeFormat(pattern="HH:mm:ss")
    @Schema(description = "营业结束时间")
    private java.util.Date businessHoursEnd;
	/**评分*/
	@Excel(name = "评分", width = 15)
    @Schema(description = "评分")
    private java.math.BigDecimal score;
	/**评论数*/
	@Excel(name = "评论数", width = 15)
    @Schema(description = "评论数")
    private java.lang.Integer numberOfComments;
	/**状态*/
	@Excel(name = "状态", width = 15, dicCode = "p_parking_lot_status")
    @Dict(dicCode = "p_parking_lot_status")
    @Schema(description = "状态")
    private java.lang.String status;
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
	/**所属部门编码*/
    @Schema(description = "所属部门编码")
    private java.lang.String sysOrgCode;
}
