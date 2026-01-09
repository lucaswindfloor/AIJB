package org.jeecg.modules.moldai.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("mold_ai_scene")
@Schema(description = "预设场景")
public class MoldAiScene implements Serializable {
    
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private String id;
    
    @Schema(description = "场景编码")
    private String sceneCode;
    
    @Schema(description = "场景名称")
    private String sceneName;
    
    @Schema(description = "材料等级(1.0-6.0)")
    private BigDecimal materialLevel;
    
    @Schema(description = "低风险阈值")
    private BigDecimal thresholdLow;
    
    @Schema(description = "高风险阈值")
    private BigDecimal thresholdHigh;
    
    @Schema(description = "是否启用")
    private Integer enabled;
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}


