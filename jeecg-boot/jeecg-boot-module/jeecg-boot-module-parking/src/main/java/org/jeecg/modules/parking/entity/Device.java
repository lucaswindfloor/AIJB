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

@Schema(description="硬件设备表")
@Data
@TableName("p_device")
public class Device implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private java.lang.String id;
	/**关卡ID*/
	@Excel(name = "关卡ID", width = 15)
    @Schema(description = "关卡ID")
    private java.lang.String checkpointId;
	/**设备名称*/
	@Excel(name = "设备名称", width = 15)
    @Schema(description = "设备名称")
    private java.lang.String name;
	/**设备类型*/
	@Excel(name = "设备类型", width = 15)
    @Schema(description = "设备类型")
    private java.lang.String deviceType;
	/**品牌*/
	@Excel(name = "品牌", width = 15)
    @Schema(description = "品牌")
    private java.lang.String brand;
	/**型号*/
	@Excel(name = "型号", width = 15)
    @Schema(description = "型号")
    private java.lang.String model;
	/**IP地址*/
	@Excel(name = "IP地址", width = 15)
    @Schema(description = "IP地址")
    private java.lang.String ipAddress;
	/**状态*/
	@Excel(name = "状态", width = 15)
    @Schema(description = "状态")
    private java.lang.String status;
	/**最后在线时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "最后在线时间")
    private java.util.Date lastOnlineTime;
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