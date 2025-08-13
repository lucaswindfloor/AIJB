package org.jeecg.modules.animalhusbandry.entity;

import java.io.Serializable;
import java.math.BigDecimal;
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

/**
 * @Description: 牲畜档案表
 * @Author: jeecg-boot
 * @Date:   2024-08-26
 * @Version: V1.0
 */
@Data
@TableName("ah_animal")
@Schema(name="ah_animal对象", description="牲畜档案表")
public class AhAnimal implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
	private java.lang.String id;
	/**耳标号 (唯一)*/
	@Excel(name = "耳标号", width = 15)
    @Schema(description = "耳标号 (唯一)")
	private java.lang.String earTagId;
	/**牲畜昵称*/
	@Excel(name = "牲畜昵称", width = 15)
    @Schema(description = "牲畜昵称")
	private java.lang.String name;
	/**牲畜类型 (字典: animal_type, 如黄牛、奶牛)*/
	@Excel(name = "牲畜类型", width = 15, dicCode = "animal_type")
	@Dict(dicCode = "animal_type")
    @Schema(description = "牲畜类型 (字典: animal_type, 如黄牛、奶牛)")
	private java.lang.String type;
	/**品种 (字典: animal_breed, 如安格斯牛)*/
	@Excel(name = "品种", width = 15, dicCode = "animal_breed")
	@Dict(dicCode = "animal_breed")
	@Schema(description = "品种 (字典: animal_breed, 如安格斯牛)")
	private java.lang.String breed;
	/**来源 (字典: animal_source, 如外购)*/
	@Excel(name = "来源", width = 15, dicCode = "animal_source")
	@Dict(dicCode = "animal_source")
	@Schema(description = "来源 (字典: animal_source, 如外购)")
	private java.lang.String source;
	/**所属畜群ID (关联 ah_herd.id)*/
	@Excel(name = "所属畜群", width = 15, dictTable = "ah_herd", dicText = "name", dicCode = "id")
    @Dict(dictTable = "ah_herd", dicText = "name", dicCode = "id")
    @Schema(description = "所属畜群ID (关联 ah_herd.id)")
	private java.lang.String herdId;
	/**所在围栏编号*/
	@Excel(name = "所在围栏", width = 15)
	@Schema(description = "所在围栏编号")
	private java.lang.String enclosure;
	/**性别 (字典: sex)*/
	@Excel(name = "性别", width = 15, dicCode = "sex")
	@Dict(dicCode = "sex")
    @Schema(description = "性别 (字典: sex)")
	private java.lang.String gender;
	/**出生日期*/
	@Excel(name = "出生日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Schema(description = "出生日期")
	private java.util.Date birthDate;
	/**最新体重(KG)*/
	@Excel(name = "最新体重(KG)", width = 15)
    @Schema(description = "最新体重(KG)")
	private java.math.BigDecimal weightKg;
	/**最新体高(CM)*/
	@Excel(name = "最新体高(CM)", width = 15)
	@Schema(description = "最新体高(CM)")
	private java.math.BigDecimal height;
	/**健康状态 (字典: health_status, 如HEALTHY, SUB_HEALTHY, ALARM)*/
	@Excel(name = "健康状态", width = 15, dicCode = "health_status")
	@Dict(dicCode = "health_status")
    @Schema(description = "健康状态 (字典: health_status, 如HEALTHY, SUB_HEALTHY, ALARM)")
	private java.lang.String healthStatus;
	/**健康评分 (0-100)*/
	@Excel(name = "健康评分", width = 15)
    @Schema(description = "健康评分 (0-100)")
	private java.lang.Integer healthScore;
    /**最新体温*/
    @Excel(name = "最新体温", width = 15)
    @Schema(description = "最新体温")
    private java.math.BigDecimal latestTemperature;
    /**最新步数*/
    @Excel(name = "最新步数", width = 15)
    @Schema(description = "最新步数")
    private java.lang.Integer latestSteps;
    /**最新胃动量*/
    @Excel(name = "最新胃动量", width = 15)
    @Schema(description = "最新胃动量")
    private java.lang.String latestGastricMomentum;
	/**最新AI分析结论*/
	@Excel(name = "最新AI分析结论", width = 15)
    @Schema(description = "最新AI分析结论")
	private java.lang.String aiConclusion;
	/**最后更新经度*/
	@Excel(name = "最后更新经度", width = 15)
    @Schema(description = "最后更新经度")
	private java.math.BigDecimal lastLocationLon;
	/**最后更新纬度*/
	@Excel(name = "最后更新纬度", width = 15)
    @Schema(description = "最后更新纬度")
	private java.math.BigDecimal lastLocationLat;
	/**创建人*/
    @Schema(description = "创建人")
	private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建日期")
	private java.util.Date createTime;
	/**更新人*/
    @Schema(description = "更新人")
	private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新日期")
	private java.util.Date updateTime;
} 