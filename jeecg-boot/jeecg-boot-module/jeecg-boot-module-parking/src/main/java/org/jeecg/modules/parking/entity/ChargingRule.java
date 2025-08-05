package org.jeecg.modules.parking.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description="计费规则表")
@Data
@TableName("p_charging_rule")
public class ChargingRule implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private java.lang.String id;
	/**停车场ID*/
	@Excel(name = "停车场ID", width = 15)
    @Schema(description = "停车场ID")
    private java.lang.String parkingLotId;
	/**规则名称*/
	@Excel(name = "规则名称", width = 15)
    @Schema(description = "规则名称")
    private java.lang.String name;
	/**车辆类型ID*/
	@Excel(name = "车辆类型ID", width = 15)
    @Schema(description = "车辆类型ID")
    private java.lang.String vehicleTypeId;
	/**计费类型*/
	@Excel(name = "计费类型", width = 15)
    @Schema(description = "计费类型")
    private java.lang.String ruleType;
	/**计费模式*/
	@Excel(name = "计费模式", width = 15)
    @Schema(description = "计费模式")
    private java.lang.String billingModel;
	/**分时段收费规则 (JSON格式)*/
	@Excel(name = "分时段收费规则 (JSON格式)", width = 15)
    @Schema(description = "分时段收费规则 (JSON格式)")
    private java.lang.String timeSegmentsJson;
	/**单价 (按次收费时使用)*/
	@Excel(name = "单价 (按次收费时使用)", width = 15)
    @Schema(description = "单价 (按次收费时使用)")
    private java.math.BigDecimal rate;
	/**每日收费上限(封顶)*/
	@Excel(name = "每日收费上限(封顶)", width = 15)
    @Schema(description = "每日收费上限(封顶)")
    private java.math.BigDecimal capPerDay;
	/**单次停车收费上限(封顶)*/
	@Excel(name = "单次停车收费上限(封顶)", width = 15)
    @Schema(description = "单次停车收费上限(封顶)")
    private java.math.BigDecimal capPerStay;
	/**状态*/
	@Excel(name = "状态", width = 15, dicCode = "rule_status")
    @Dict(dicCode = "rule_status")
    @Schema(description = "状态")
    private java.lang.Integer status;
} 