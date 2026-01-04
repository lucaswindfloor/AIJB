package org.jeecg.modules.animalhusbandry.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.animalhusbandry.entity.AhFence;
import org.jeecg.modules.animalhusbandry.service.IAhFenceService;
import org.jeecg.modules.animalhusbandry.service.IDashboardService;
import org.jeecg.modules.animalhusbandry.vo.DashboardKpiVo;
import org.jeecg.modules.animalhusbandry.vo.MapAnimalDataVo;
import org.jeecg.modules.animalhusbandry.vo.RecentAlarmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.List;

@Tag(name = "智慧畜牧-牧场驾驶舱")
@RestController
@RequestMapping("/animal_husbandry/dashboard")
@Slf4j
public class DashboardController {

    @Autowired
    private IDashboardService dashboardService;

    @Autowired
    private IAhFenceService fenceService;

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

    @Operation(summary = "获取所有启用的电子围栏", description = "获取所有状态为“启用”的电子围栏")
    @GetMapping(value = "/fences")
    public Result<List<AhFence>> getFences() {
        QueryWrapper<AhFence> queryWrapper = new QueryWrapper<>();
        // 状态 (1-启用, 0-禁用)
        queryWrapper.eq("status", 1);
        List<AhFence> list = fenceService.list(queryWrapper);
        return Result.OK(list);
    }

    @Operation(summary = "获取越界告警列表", description = "获取所有不在任何启用状态的电子围栏内的牲畜列表")
    @GetMapping(value = "/out-of-bounds-alarms")
    public Result<List<RecentAlarmVo>> getOutOfBoundsAlarms() {
        List<RecentAlarmVo> outOfBoundsAlarms = dashboardService.getOutOfBoundsAlarms();
        return Result.OK(outOfBoundsAlarms);
    }

    @Operation(summary = "获取所有健康告警列表", description = "获取所有健康相关的告警记录")
    @GetMapping(value = "/all-health-alarms")
    public Result<List<RecentAlarmVo>> getAllHealthAlarms() {
        List<RecentAlarmVo> allHealthAlarms = dashboardService.getAllHealthAlarms();
        return Result.OK(allHealthAlarms);
    }
} 