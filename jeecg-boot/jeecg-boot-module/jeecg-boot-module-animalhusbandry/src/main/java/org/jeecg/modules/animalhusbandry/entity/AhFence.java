package org.jeecg.modules.animalhusbandry.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jeecg.common.aspect.annotation.Dict;

/**
 * @Description: 电子围栏表
 * @Author: jeecg-boot
 * @Date:   2024-08-25
 * @Version: V1.0
 */
@Data
@TableName("ah_fence")
@Schema(description = "电子围栏表")
public class AhFence implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private java.lang.String id;
	/**围栏名称*/
	@Excel(name = "围栏名称", width = 15)
    @Schema(description = "围栏名称")
    private java.lang.String name;
	/**描述*/
	@Excel(name = "描述", width = 15)
    @Schema(description = "描述")
    private java.lang.String description;
	/**围栏顶点坐标（GeoJSON Polygon 格式）*/
	@Excel(name = "围栏顶点坐标", width = 15)
    @Schema(description = "围栏顶点坐标（GeoJSON Polygon 格式）")
    private java.lang.String points;
	/**状态 (1-启用, 0-禁用)*/
	@Excel(name = "状态", width = 15, dicCode = "fence_status")
    @Schema(description = "状态 (1-启用, 0-禁用)")
    @Dict(dicCode = "fence_status")
    private java.lang.Integer status;
	/**创建人*/
    @Schema(description = "创建人")
    private java.lang.String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private java.util.Date createTime;
	/**更新人*/
    @Schema(description = "更新人")
    private java.lang.String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private java.util.Date updateTime;
	/**所属部门*/
    @Schema(description = "所属部门")
    private java.lang.String sysOrgCode;
}

