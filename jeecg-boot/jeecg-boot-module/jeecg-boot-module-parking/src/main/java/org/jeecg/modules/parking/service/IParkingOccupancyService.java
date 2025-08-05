package org.jeecg.modules.parking.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.parking.dto.ParkingOccupancyChartDTO;
import org.jeecg.modules.parking.dto.ParkingOccupancyQuery;
import org.jeecg.modules.parking.entity.ParkingLot;
import org.jeecg.modules.parking.entity.ParkingOccupancy;
import java.util.List;

public interface IParkingOccupancyService extends IService<ParkingOccupancy> {

    List<ParkingOccupancyChartDTO> getOccupancyRateData(ParkingOccupancyQuery query);

    List<ParkingLot> listAllParkingLots();
}
