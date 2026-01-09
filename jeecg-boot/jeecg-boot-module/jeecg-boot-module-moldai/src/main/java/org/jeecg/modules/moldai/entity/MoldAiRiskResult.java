package org.jeecg.modules.moldai.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("mold_ai_risk_result")
@Schema(description = "风险计算结果")
public class MoldAiRiskResult implements Serializable {
    
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private String id;
    
    @Schema(description = "设备ID")
    private String deviceId;
    
    @Schema(description = "场景ID")
    private String sceneId;
    
    @Schema(description = "霉菌指数(MI)")
    private BigDecimal miValue;
    
    @Schema(description = "风险等级: LOW/MEDIUM/HIGH")
    private String riskLevel;
    
    @Schema(description = "计算时间")
    private Date calculatedTime;
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}


