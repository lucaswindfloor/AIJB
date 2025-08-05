package org.jeecg.modules.parking.vo;

import lombok.Data;
import org.jeecg.modules.parking.entity.ParkingRecord;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 停车记录分页查询返回对象
 */
@Data
public class ParkingRecordPage extends ParkingRecord {

    @Excel(name = "停车场名称", width = 20)
    @Schema(description = "停车场名称")
    private String parkingLotName;

    @Excel(name = "入口名称", width = 15)
    @Schema(description = "入口名称")
    private String entryCheckpointName;

    @Excel(name = "出口名称", width = 15)
    @Schema(description = "出口名称")
    private String exitCheckpointName;

    @Excel(name = "停车时长", width = 15)
    @Schema(description = "停车时长")
    private String duration;
} 