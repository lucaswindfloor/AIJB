package org.jeecg.modules.parking.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.parking.entity.ParkingOccupancy;
import org.jeecg.modules.parking.dto.ParkingOccupancyQuery;

import java.util.List;

public interface ParkingOccupancyMapper extends BaseMapper<ParkingOccupancy> {
    List<ParkingOccupancy> selectListByQuery(@Param("query") ParkingOccupancyQuery query);
} 