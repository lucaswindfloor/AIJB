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
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @Description: 牲畜设备关联表
 * @Author: jeecg-boot
 * @Date:   2024-08-26
 * @Version: V1.0
 */
@Data
@TableName("ah_animal_device_link")
@Schema(name="ah_animal_device_link对象", description="牲畜设备关联表")
public class AhAnimalDeviceLink implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
	private java.lang.String id;
	/**牲畜ID*/
	@Excel(name = "牲畜ID", width = 15)
    @Schema(description = "牲畜ID")
	private java.lang.String animalId;
	/**设备ID*/
	@Excel(name = "设备ID", width = 15)
    @Schema(description = "设备ID")
	private java.lang.String deviceId;
	/**绑定时间*/
	@Excel(name = "绑定时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "绑定时间")
	private java.util.Date bindTime;
	/**解绑时间*/
	@Excel(name = "解绑时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "解绑时间")
	private java.util.Date unbindTime;
	/**是否当前有效 (1-是, 0-否)*/
	@Excel(name = "是否当前有效", width = 15, dicCode = "yn")
    @Schema(description = "是否当前有效 (1-是, 0-否)")
	private java.lang.Integer isActive;
	/**设备类型 (冗余字段, 用于简化查询)*/
	@Excel(name = "设备类型", width = 15)
	@Schema(description = "设备类型 (冗余字段, 用于简化查询)")
	private java.lang.String deviceType;
} 