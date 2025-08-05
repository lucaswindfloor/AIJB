package org.jeecg.modules.animalhusbandry.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@Schema(description = "用于封装驾驶舱右侧实时告警列表的VO")
public class RecentAlarmVo {

    @Schema(description = "告警记录ID")
    private String id;

    @Schema(description = "牲畜ID")
    private String animalId;

    @Schema(description = "牲畜耳标号")
    private String earTagId;

    @Schema(description = "告警内容描述")
    private String alarmContent;

    @Schema(description = "告警级别 (WARN, CRITICAL)")
    private String alarmLevel;
    
    @Schema(description = "告警时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date alarmTime;
} 