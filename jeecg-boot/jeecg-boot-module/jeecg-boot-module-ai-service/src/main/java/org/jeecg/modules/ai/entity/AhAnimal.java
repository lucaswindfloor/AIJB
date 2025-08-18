package org.jeecg.modules.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("ah_animal")
@Schema(name="ah_animal对象", description="牲畜档案表")
public class AhAnimal implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private java.lang.String id;

    @Excel(name = "耳标号", width = 15)
    @Schema(description = "耳标号 (唯一)")
    private java.lang.String earTagId;

    @Excel(name = "健康状态", width = 15, dicCode = "health_status")
    @Dict(dicCode = "health_status")
    @Schema(description = "健康状态 (字典: health_status, 如HEALTHY, SUB_HEALTHY, ALARM)")
    private java.lang.String healthStatus;

    @Excel(name = "健康评分", width = 15)
    @Schema(description = "健康评分 (0-100)")
    private java.lang.Integer healthScore;

    @Excel(name = "最新AI分析结论", width = 15)
    @Schema(description = "最新AI分析结论")
    private java.lang.String aiConclusion;

    @Schema(description = "创建人")
    private java.lang.String createBy;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建日期")
    private java.util.Date createTime;

    @Schema(description = "更新人")
    private java.lang.String updateBy;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新日期")
    private java.util.Date updateTime;
}
