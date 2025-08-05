package org.jeecg.modules.animalhusbandry.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.modules.animalhusbandry.entity.AhAnimal;
import org.jeecg.modules.animalhusbandry.entity.AhDevice;
import org.jeecg.modules.animalhusbandry.mapper.AhAlarmRecordMapper;
import org.jeecg.modules.animalhusbandry.mapper.AhAnimalMapper;
import org.jeecg.modules.animalhusbandry.mapper.AhDeviceMapper;
import org.jeecg.modules.animalhusbandry.service.IDashboardService;
import org.jeecg.modules.animalhusbandry.vo.DashboardKpiVo;
import org.jeecg.modules.animalhusbandry.vo.MapAnimalDataVo;
import org.jeecg.modules.animalhusbandry.vo.RecentAlarmVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements IDashboardService {

    @Resource
    private AhAnimalMapper ahAnimalMapper;

    @Resource
    private AhDeviceMapper ahDeviceMapper;
    
    @Resource
    private AhAlarmRecordMapper ahAlarmRecordMapper;

    private static final int RECENT_ALARMS_LIMIT = 10;

    @Override
    public DashboardKpiVo getKpiData() {
        DashboardKpiVo kpiVo = new DashboardKpiVo();

        // 1. 牲畜总数
        kpiVo.setTotalAnimals(Math.toIntExact(ahAnimalMapper.selectCount(null)));
        
        // 2. 设备统计
        kpiVo.setTotalDevices(Math.toIntExact(ahDeviceMapper.selectCount(null)));
        QueryWrapper<AhDevice> deviceQueryWrapper = new QueryWrapper<>();
        deviceQueryWrapper.eq("status", "ACTIVE"); // 'ACTIVE' or your equivalent status for online
        kpiVo.setOnlineDevices(Math.toIntExact(ahDeviceMapper.selectCount(deviceQueryWrapper)));
        
        // 3. 健康状态分布
        List<Map<String, Object>> healthStatusCounts = ahAnimalMapper.countByHealthStatus();
        Map<String, Long> healthStatusMap = healthStatusCounts.stream()
                .collect(Collectors.toMap(
                        row -> (String) row.get("health_status"),
                        row -> (Long) row.get("count"),
                        (v1, v2) -> v1 // In case of duplicate keys, which shouldn't happen with GROUP BY
                ));

        kpiVo.setHealthyCount(healthStatusMap.getOrDefault("HEALTHY", 0L).intValue());
        kpiVo.setSubHealthyCount(healthStatusMap.getOrDefault("SUB_HEALTHY", 0L).intValue());
        kpiVo.setAlarmCount(healthStatusMap.getOrDefault("ALARM", 0L).intValue());

        // 4. 今日新增告警
        kpiVo.setNewAlarmsToday(ahAlarmRecordMapper.countNewAlarmsToday());

        return kpiVo;
    }

    @Override
    public List<MapAnimalDataVo> getMapAnimalData() {
        QueryWrapper<AhAnimal> queryWrapper = new QueryWrapper<>();
        // 只查询有坐标的牲畜
        queryWrapper.isNotNull("last_location_lon").isNotNull("last_location_lat");

        List<AhAnimal> animalList = ahAnimalMapper.selectList(queryWrapper);

        // 转换为VO
        return animalList.stream().map(animal -> {
            MapAnimalDataVo vo = new MapAnimalDataVo();
            vo.setAnimalId(animal.getId());
            vo.setEarTagId(animal.getEarTagId());
            vo.setLon(animal.getLastLocationLon().doubleValue());
            vo.setLat(animal.getLastLocationLat().doubleValue());
            vo.setHealthStatus(animal.getHealthStatus());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<RecentAlarmVo> getRecentAlarms() {
        return ahAlarmRecordMapper.getRecentAlarms(RECENT_ALARMS_LIMIT);
    }
} 