package org.jeecg.modules.parking.controller;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.parking.dto.ParkingOccupancyChartDTO;
import org.jeecg.modules.parking.dto.ParkingOccupancyQuery;
import org.jeecg.modules.parking.entity.ParkingLot;
import org.jeecg.modules.parking.service.IParkingLotService;
import org.jeecg.modules.parking.service.IParkingOccupancyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/parking")
public class ParkingOccupancyController {

    @Autowired
    private IParkingOccupancyService parkingOccupancyService;

    @Autowired
    private IParkingLotService parkingLotService;

    /**
     * To be consistent with the original system, this endpoint accepts a query object.
     */
    @GetMapping("/analysis")
    public Result<?> getParkingOccupancyRate(ParkingOccupancyQuery query) {
        try {
            List<ParkingOccupancyChartDTO> data = parkingOccupancyService.getOccupancyRateData(query);
            return Result.OK(data);
        } catch (Exception e) {
            log.error("Failed to get parking occupancy rate", e);
            return Result.error("Query failed: " + e.getMessage());
        }
    }

    /**
     * New endpoint to get all parking lots for dropdowns.
     */
    @GetMapping("/listAll")
    public Result<?> listAll() {
        try {
            List<ParkingLot> list = parkingOccupancyService.listAllParkingLots();
            return Result.OK(list);
        } catch (Exception e) {
            log.error("Failed to get all parking lots", e);
            return Result.error("Query failed: " + e.getMessage());
        }
    }

    /**
     * A simple test endpoint to verify if the controller is loaded.
     */
    @GetMapping("/hello")
    public Result<String> hello() {
        return Result.OK("Hello from ParkingOccupancyController!");
    }
}
