package org.jeecg.modules.animalhusbandry.service;

import org.jeecg.modules.animalhusbandry.vo.DashboardKpiVo;
import org.jeecg.modules.animalhusbandry.vo.MapAnimalDataVo;
import org.jeecg.modules.animalhusbandry.vo.RecentAlarmVo;

import java.util.List;

/**
 * 牧场驾驶舱服务接口
 */
public interface IDashboardService {

    /**
     * 获取页面顶部的核心KPI统计数据
     * @return DashboardKpiVo
     */
    DashboardKpiVo getKpiData();

    /**
     * 获取所有需要在地图上显示的牲畜数据列表
     * @return List<MapAnimalDataVo>
     */
    List<MapAnimalDataVo> getMapAnimalData();

    /**
     * 获取最新的N条告警记录
     * @return List<RecentAlarmVo>
     */
    List<RecentAlarmVo> getRecentAlarms();

    /**
     * 获取所有越界的牲畜列表
     * @return List<RecentAlarmVo>
     */
    List<RecentAlarmVo> getOutOfBoundsAlarms();

    /**
     * 获取所有健康告警的列表
     * @return List<RecentAlarmVo>
     */
    List<RecentAlarmVo> getAllHealthAlarms();
} 