package org.jeecg.modules.animalhusbandry.entity;

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
@TableName("ah_animal_lifecycle_event")
@Schema(name = "ah_animal_lifecycle_event对象", description = "牲畜生命周期事件表")
public class AhAnimalLifecycleEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private String id;

    @Schema(description = "关联的牲畜ID")
    private String animalId;

    @Excel(name = "事件类型", width = 15, dicCode = "animal_event_type")
    @Schema(description = "事件类型 (字典: animal_event_type)")
    private String eventType;

    @Excel(name = "事件发生时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "事件发生时间")
    private Date eventTime;

    @Excel(name = "事件详细描述", width = 30)
    @Schema(description = "事件详细描述")
    private String description;

    @Excel(name = "创建人", width = 15)
    @Schema(description = "创建人")
    private String createBy;

    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private Date createTime;
} 