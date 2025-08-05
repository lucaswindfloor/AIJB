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

@Data
@TableName("ah_alarm_record")
@Schema(name="ah_alarm_record对象", description="告警记录表")
public class AhAlarmRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private java.lang.String id;

    @Excel(name = "告警牲畜ID", width = 15)
    @Schema(description = "告警牲畜ID")
    private java.lang.String animalId;

    @Excel(name = "告警类型", width = 15, dicCode = "alarm_type")
    @Dict(dicCode = "alarm_type")
    @Schema(description = "告警类型")
    private java.lang.String alarmType;

    @Excel(name = "告警级别", width = 15, dicCode = "alarm_level")
    @Dict(dicCode = "alarm_level")
    @Schema(description = "告警级别")
    private java.lang.String alarmLevel;

    @Excel(name = "告警内容描述", width = 15)
    @Schema(description = "告警内容描述")
    private java.lang.String alarmContent;

    @Excel(name = "告警发生时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "告警发生时间")
    private java.util.Date alarmTime;

    @Excel(name = "处理状态", width = 15, dicCode = "process_status")
    @Dict(dicCode = "process_status")
    @Schema(description = "处理状态")
    private java.lang.String status;

    @Excel(name = "处理人ID", width = 15)
    @Schema(description = "处理人ID")
    private java.lang.String handlerId;

    @Excel(name = "处理时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "处理时间")
    private java.util.Date handleTime;

    @Excel(name = "处理备注", width = 15)
    @Schema(description = "处理备注")
    private java.lang.String handleNotes;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private java.util.Date createTime;
} 