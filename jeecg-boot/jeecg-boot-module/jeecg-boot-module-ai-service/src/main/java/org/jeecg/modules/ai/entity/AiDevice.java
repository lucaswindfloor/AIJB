package org.jeecg.modules.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * @Description: 设备实体类（AI服务内部使用，遵循Ai前缀和OpenAPI 3规范）
 * @Author: AI Assistant
 * @Date:   2024-08-24
 * @Version: V1.0
 */
@Data
@TableName("ah_device")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "设备台账")
public class AiDevice implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;

	/**dev_eui*/
	@Excel(name = "dev_eui", width = 15)
    @Schema(description = "dev_eui")
    private String devEui;
}
