package org.jeecg.modules.parking.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.parking.dto.ParkingOccupancyChartDTO;
import org.jeecg.modules.parking.dto.ParkingOccupancyQuery;
import org.jeecg.modules.parking.entity.ParkingLot;
import org.jeecg.modules.parking.entity.ParkingOccupancy;
import org.jeecg.modules.parking.mapper.ParkingLotMapper;
import org.jeecg.modules.parking.mapper.ParkingOccupancyMapper;
import org.jeecg.modules.parking.service.IParkingOccupancyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ParkingOccupancyServiceImpl extends ServiceImpl<ParkingOccupancyMapper, ParkingOccupancy> implements IParkingOccupancyService {

    @Autowired
    private ParkingLotMapper parkingLotMapper;

    @Override
    public List<ParkingOccupancyChartDTO> getOccupancyRateData(ParkingOccupancyQuery query) {
        if (query == null || !StringUtils.hasText(query.getParkingLotIds())) {
            return Collections.emptyList();
        }

        // Re-implementing the logic from scratch to be absolutely safe.
        // A new, clean query object is created for every single call.
        ParkingOccupancyQuery finalQuery = new ParkingOccupancyQuery();
        finalQuery.setParkingLotIds(query.getParkingLotIds());
        finalQuery.setGranularity(query.getGranularity());
        
        String granularity = query.getGranularity();

        if ("day".equals(granularity)) {
            LocalDate date = StringUtils.hasText(query.getDate()) ? LocalDate.parse(query.getDate()) : LocalDate.now();
            finalQuery.setStartDate(date.toString());
            finalQuery.setEndDate(date.toString());
        } else if ("week".equals(granularity)) {
            LocalDate date = StringUtils.hasText(query.getDate()) ? LocalDate.parse(query.getDate()) : LocalDate.now();
            LocalDate startOfWeek = date.with(WeekFields.ISO.dayOfWeek(), 1);
            LocalDate endOfWeek = startOfWeek.plusDays(6);
            finalQuery.setStartDate(startOfWeek.toString());
            finalQuery.setEndDate(endOfWeek.toString());
        } else if ("month".equals(granularity)) {
            LocalDate date = StringUtils.hasText(query.getDate()) ? LocalDate.parse(query.getDate()) : LocalDate.now();
            YearMonth yearMonth = YearMonth.from(date);
            finalQuery.setStartDate(yearMonth.atDay(1).toString());
            finalQuery.setEndDate(yearMonth.atEndOfMonth().toString());
        } else if ("custom".equals(granularity)) {
            finalQuery.setStartDate(query.getStartDate());
            finalQuery.setEndDate(query.getEndDate());
        } else {
            // Default or unknown granularity, return empty
            return Collections.emptyList();
        }

        List<ParkingOccupancy> occupancyList = baseMapper.selectListByQuery(finalQuery);
        if (occupancyList == null || occupancyList.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> parkingLotIdStrList = Arrays.asList(finalQuery.getParkingLotIds().split(","));
        List<Long> parkingLotIdList = parkingLotIdStrList.stream().map(Long::parseLong).collect(Collectors.toList());

        if (parkingLotIdList.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, String> parkingLotNameMap = getParkingLotNameMap(parkingLotIdList);

        return occupancyList.stream()
                .map(item -> convertToDto(item, finalQuery.getGranularity(), parkingLotNameMap))
                .collect(Collectors.toList());
    }

    private Map<String, String> getParkingLotNameMap(List<Long> parkingLotIdList) {
        List<ParkingLot> parkingLots = parkingLotMapper.selectBatchIds(parkingLotIdList);
        return parkingLots.stream()
                .collect(Collectors.toMap(ParkingLot::getId, ParkingLot::getName, (k1, k2) -> k1));
    }

    private ParkingOccupancyChartDTO convertToDto(ParkingOccupancy item, String granularity, Map<String, String> nameMap) {
        ParkingOccupancyChartDTO dto = new ParkingOccupancyChartDTO();
        String parkingLotId = item.getParkingLotId();
        dto.setParkingLotName(nameMap.get(parkingLotId));
        dto.setOccupancyRate(item.getOccupancyRate() != null ? item.getOccupancyRate().doubleValue() : 0.0);

        if ("day".equals(granularity)) {
            dto.setTime(String.format("%02d:00", item.getHour()));
        } else {
            dto.setTime(item.getDate());
        }
        return dto;
    }
    
    @Override
    public List<ParkingLot> listAllParkingLots() {
        return parkingLotMapper.selectList(Wrappers.lambdaQuery(ParkingLot.class));
    }
}
