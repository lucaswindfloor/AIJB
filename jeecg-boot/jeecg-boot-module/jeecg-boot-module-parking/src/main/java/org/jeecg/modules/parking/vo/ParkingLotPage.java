package org.jeecg.modules.parking.vo;

import java.util.List;
import org.jeecg.modules.parking.entity.ParkingLot;
import org.jeecg.modules.parking.entity.ParkingLotConfig;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelEntity;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;

/**
 * @Description: 停车场核心信息表
 * @Author: jeecg-boot
 * @Date:   2024-08-10
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "停车场核心信息表Page")
public class ParkingLotPage extends ParkingLot {

	private static final long serialVersionUID = 1L;

	@Schema(description="停车场业务配置表")
	private ParkingLotConfig parkingLotConfig;

	@Schema(description="已用车位")
	private Integer usedSpaces;

	@Schema(description="关卡数量")
	private Integer checkPointCounts;

} 