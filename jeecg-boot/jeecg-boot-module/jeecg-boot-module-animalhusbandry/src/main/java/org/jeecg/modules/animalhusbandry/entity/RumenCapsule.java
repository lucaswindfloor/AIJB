package org.jeecg.modules.animalhusbandry.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @Description: 瘤胃胶囊设备表
 * @Author: jeecg-boot
 * @Date:   2024-08-20
 * @Version: V1.0
 */
@Data
@TableName("ah_rumen_capsule")
@Schema(name="ah_rumen_capsule", description="瘤胃胶囊设备表")
public class RumenCapsule implements Serializable {
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
	/**胶囊名称*/
	@Excel(name = "胶囊名称", width = 15)
    @Schema(description = "胶囊名称")
    private java.lang.String capsuleName;
	/**DevEUI*/
	@Excel(name = "DevEUI", width = 15)
    @Schema(description = "DevEUI")
    @TableField("dev_eui")
    private java.lang.String devEui;
	/**AppEUI*/
	@Excel(name = "AppEUI", width = 15)
    @Schema(description = "AppEUI")
    @TableField("app_eui")
    private java.lang.String appEui;
	/**AppKey*/
	@Excel(name = "AppKey", width = 15)
    @Schema(description = "AppKey")
    @TableField("app_key")
    private java.lang.String appKey;
	/**设备状态*/
	@Excel(name = "设备状态", width = 15, dicCode = "device_status")
    @Schema(description = "设备状态")
    private java.lang.String status;
	/**ThingsBoard设备ID*/
	@Excel(name = "ThingsBoard设备ID", width = 15)
    @Schema(description = "ThingsBoard设备ID")
    @TableField("tb_device_id")
    private java.lang.String tbDeviceId;
}