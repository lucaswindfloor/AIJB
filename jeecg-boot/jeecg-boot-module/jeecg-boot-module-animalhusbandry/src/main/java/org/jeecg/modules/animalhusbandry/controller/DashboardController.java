package org.jeecg.modules.animalhusbandry.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.animalhusbandry.service.IDashboardService;
import org.jeecg.modules.animalhusbandry.vo.DashboardKpiVo;
import org.jeecg.modules.animalhusbandry.vo.MapAnimalDataVo;
import org.jeecg.modules.animalhusbandry.vo.RecentAlarmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "智慧畜牧-牧场驾驶舱")
@RestController
@RequestMapping("/animal_husbandry/dashboard")
@Slf4j
public class DashboardController {

    @Autowired
    private IDashboardService dashboardService;

    @Operation(summary = "获取驾驶舱KPI统计数据", description = "获取页面顶部的核心KPI统计数据")
    @GetMapping(value = "/kpi")
    public Result<DashboardKpiVo> getKpiData() {
        DashboardKpiVo kpiData = dashboardService.getKpiData();
        return Result.OK(kpiData);
    }

    @Operation(summary = "获取地图牲畜数据", description = "获取所有需要在地图上显示的牲畜数据列表")
    @GetMapping(value = "/map-data")
    public Result<List<MapAnimalDataVo>> getMapAnimalData() {
        List<MapAnimalDataVo> mapData = dashboardService.getMapAnimalData();
        return Result.OK(mapData);
    }

    @Operation(summary = "获取近期告警列表", description = "获取最新的N条告警记录")
    @GetMapping(value = "/recent-alarms")
    public Result<List<RecentAlarmVo>> getRecentAlarms() {
        List<RecentAlarmVo> recentAlarms = dashboardService.getRecentAlarms();
        return Result.OK(recentAlarms);
    }
} 