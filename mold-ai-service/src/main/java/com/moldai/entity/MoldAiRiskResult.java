package com.moldai.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("mai_risk_result")
public class MoldAiRiskResult implements Serializable {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    private String deviceId;
    
    private String sceneId;
    
    /**
     * 霉菌指数(MI)
     */
    private BigDecimal miValue;
    
    /**
     * 风险等级: LOW/MEDIUM/HIGH
     */
    private String riskLevel;
    
    private Date calculatedTime;
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}

