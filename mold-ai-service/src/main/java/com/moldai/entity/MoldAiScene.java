package com.moldai.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("mai_scene")
public class MoldAiScene implements Serializable {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    private String sceneCode;
    
    private String sceneName;
    
    /**
     * 材料等级(1.0-6.0)
     */
    private BigDecimal materialLevel;
    
    /**
     * 低风险阈值
     */
    private BigDecimal thresholdLow;
    
    /**
     * 高风险阈值
     */
    private BigDecimal thresholdHigh;
    
    private Integer enabled;
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}

