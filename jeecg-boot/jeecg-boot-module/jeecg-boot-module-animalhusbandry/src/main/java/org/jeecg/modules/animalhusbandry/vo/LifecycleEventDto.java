package org.jeecg.modules.animalhusbandry.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

@Data
@Schema(name = "生命周期事件DTO", description = "用于封装添加生命周期事件的请求参数")
public class LifecycleEventDto {

    @Schema(description = "关联的牲畜ID", required = true)
    private String animalId;

    @Schema(description = "事件类型 (字典: animal_event_type)", required = true)
    private String eventType;

    @Schema(description = "事件发生时间", required = true)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date eventTime;

    @Schema(description = "事件详细描述")
    private String description;
} 