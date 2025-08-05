package org.jeecg.modules.parking.dto;

import lombok.Data;

@Data
public class ParkingOccupancyChartDTO {
    private String time;  // 格式：YYYY-MM-DD HH:mm:ss
    private Double occupancyRate;
    private String parkingLotName;
    private Integer totalSpaces;
    private Integer usedSpaces;
}
