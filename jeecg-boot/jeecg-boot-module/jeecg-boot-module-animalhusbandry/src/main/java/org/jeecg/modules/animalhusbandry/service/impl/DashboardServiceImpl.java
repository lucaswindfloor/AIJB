package org.jeecg.modules.animalhusbandry.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeecg.modules.animalhusbandry.entity.AhAnimal;
import org.jeecg.modules.animalhusbandry.entity.AhDevice;
import org.jeecg.modules.animalhusbandry.entity.AhFence;
import org.jeecg.modules.animalhusbandry.mapper.AhAlarmRecordMapper;
import org.jeecg.modules.animalhusbandry.mapper.AhAnimalMapper;
import org.jeecg.modules.animalhusbandry.mapper.AhDeviceMapper;
import org.jeecg.modules.animalhusbandry.mapper.AhFenceMapper;
import org.jeecg.modules.animalhusbandry.service.IDashboardService;
import org.jeecg.modules.animalhusbandry.vo.DashboardKpiVo;
import org.jeecg.modules.animalhusbandry.vo.MapAnimalDataVo;
import org.jeecg.modules.animalhusbandry.vo.RecentAlarmVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DashboardServiceImpl implements IDashboardService {

    private static final Logger log = LoggerFactory.getLogger(DashboardServiceImpl.class);

    @Resource
    private AhAnimalMapper ahAnimalMapper;

    @Resource
    private AhDeviceMapper ahDeviceMapper;
    
    @Resource
    private AhAlarmRecordMapper ahAlarmRecordMapper;

    @Resource
    private AhFenceMapper ahFenceMapper;

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

    @Override
    public List<RecentAlarmVo> getOutOfBoundsAlarms() {
        // 1. 获取所有启用的围栏
        QueryWrapper<AhFence> fenceQuery = new QueryWrapper<>();
        fenceQuery.eq("status", 1);
        List<AhFence> enabledFences = ahFenceMapper.selectList(fenceQuery);

        // 如果没有启用中的围栏，则不存在“越界”的概念，直接返回空列表
        if (enabledFences.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 获取所有有坐标的牲畜
        QueryWrapper<AhAnimal> animalQuery = new QueryWrapper<>();
        animalQuery.isNotNull("last_location_lon").isNotNull("last_location_lat");
        List<AhAnimal> animalsWithLocation = ahAnimalMapper.selectList(animalQuery);

        // 3. 利用JTS进行空间计算
        GeometryFactory geometryFactory = new GeometryFactory();
        ObjectMapper objectMapper = new ObjectMapper();
        List<Polygon> fencePolygons = new ArrayList<>();

        // 将围栏的JSON坐标点转换为JTS的Polygon对象
        for (AhFence fence : enabledFences) {
            // [健壮性修复] 增加对points字段的空值和空字符串检查
            if (fence.getPoints() == null || fence.getPoints().trim().isEmpty()) {
                continue;
            }
            try {
                List<List<Double>> points = objectMapper.readValue(fence.getPoints(), new TypeReference<List<List<Double>>>() {});
                Coordinate[] coordinates = points.stream()
                        .map(p -> new Coordinate(p.get(0), p.get(1)))
                        .toArray(Coordinate[]::new);
                // JTS要求多边形闭合，即第一个点和最后一个点相同
                if (coordinates.length > 0 && !coordinates[0].equals(coordinates[coordinates.length - 1])) {
                    Coordinate[] closedCoordinates = new Coordinate[coordinates.length + 1];
                    System.arraycopy(coordinates, 0, closedCoordinates, 0, coordinates.length);
                    closedCoordinates[coordinates.length] = coordinates[0];
                    coordinates = closedCoordinates;
                }
                if (coordinates.length >= 4) {
                    fencePolygons.add(geometryFactory.createPolygon(coordinates));
                }
            } catch (Exception e) {
                log.error("解析围栏坐标失败: {}, fenceId: {}", fence.getName(), fence.getId(), e);
            }
        }

        List<RecentAlarmVo> outOfBoundsAnimals = new ArrayList<>();
        for (AhAnimal animal : animalsWithLocation) {
            Coordinate animalCoordinate = new Coordinate(animal.getLastLocationLon().doubleValue(), animal.getLastLocationLat().doubleValue());
            boolean isInAnyFence = fencePolygons.stream()
                    .anyMatch(polygon -> polygon.contains(geometryFactory.createPoint(animalCoordinate)));

            if (!isInAnyFence) {
                RecentAlarmVo vo = new RecentAlarmVo();
                vo.setAnimalId(animal.getId());
                vo.setEarTagId(animal.getEarTagId());
                vo.setAlarmContent("超出电子围栏范围");
                // vo.setAlarmTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())); // 可选：设置为当前时间
                outOfBoundsAnimals.add(vo);
            }
        }

        return outOfBoundsAnimals;
    }

    @Override
    public List<RecentAlarmVo> getAllHealthAlarms() {
        return ahAlarmRecordMapper.getAllAlarms();
    }
} 