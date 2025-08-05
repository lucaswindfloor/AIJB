package org.jeecg.modules.parking.dto;

import lombok.Data;
import java.util.List;

@Data
public class ParkingOccupancyQuery {
    private String parkingLotIds; // Comma-separated list of IDs, e.g., "1,2,3"
    private String granularity;  // day/week/month/custom
    private String date; // Base date for day/week/month query
    private String startDate; // Start date for custom query
    private String endDate; // End date for custom query
    private String orderBy;
    private Integer page;
    private Integer size;
}
