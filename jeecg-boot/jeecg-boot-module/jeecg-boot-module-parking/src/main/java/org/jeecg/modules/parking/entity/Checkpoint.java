package org.jeecg.modules.parking.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description="关卡/出入口表")
@Data
@TableName("p_checkpoint")
public class Checkpoint implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键ID*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private java.lang.String id;
	/**停车场ID*/
	@Excel(name = "停车场ID", width = 15)
    @Schema(description = "停车场ID")
    private java.lang.String parkingLotId;
	/**关卡名称*/
	@Excel(name = "关卡名称", width = 15)
    @Schema(description = "关卡名称")
    private java.lang.String name;
	/**方向*/
	@Excel(name = "方向", width = 15)
    @Schema(description = "方向")
    private java.lang.String direction;
	/**状态*/
	@Excel(name = "状态", width = 15)
    @Schema(description = "状态")
    private java.lang.String status;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @Schema(description = "备注")
    private java.lang.String remark;
} 