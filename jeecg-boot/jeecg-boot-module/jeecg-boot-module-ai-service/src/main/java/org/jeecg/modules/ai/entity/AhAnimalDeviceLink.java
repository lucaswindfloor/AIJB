package org.jeecg.modules.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("ah_animal_device_link")
@Schema(name="ah_animal_device_link对象", description="牲畜设备关联表")
public class AhAnimalDeviceLink implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private java.lang.String id;

    @Excel(name = "牲畜ID", width = 15)
    @Schema(description = "牲畜ID")
    private java.lang.String animalId;

    @Excel(name = "设备ID", width = 15)
    @Schema(description = "设备ID")
    private java.lang.String deviceId;

    @Excel(name = "绑定时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "绑定时间")
    private java.util.Date bindTime;

    @Excel(name = "解绑时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "解绑时间")
    private java.util.Date unbindTime;

    @Excel(name = "是否当前有效", width = 15)
    @Schema(description = "是否当前有效 (1-是, 0-否)")
    private java.lang.Integer isActive;

    @Excel(name = "设备类型", width = 15)
    @Schema(description = "设备类型 (冗余字段, 用于简化查询)")
    private java.lang.String deviceType;
}
