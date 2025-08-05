package org.jeecg.modules.animalhusbandry.vo;

import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(name="设备同步DTO", description="从前端接收用于同步的设备信息")
public class AhDeviceSyncDTO {

    @Schema(description = "LoRaWAN DevEUI (唯一)", required = true)
    private String devEui;

    @Schema(description = "设备名称/别名")
    private String name;

    @Schema(description = "设备类型 (字典: device_type)")
    private String deviceType;

    @Schema(description = "设备型号")
    private String model;

    @Schema(description = "采购日期", example = "2024-08-26")
    private java.util.Date purchaseDate;
} 