package org.jeecg.modules.animalhusbandry.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.animalhusbandry.entity.AhDevice;
import org.jeecg.modules.animalhusbandry.vo.DeviceKpiVo;
import org.jeecg.modules.animalhusbandry.vo.ProblematicDeviceVo;
import org.jeecg.modules.animalhusbandry.vo.AhDevicePage;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * @Description: 设备信息表
 * @Author: jeecg-boot
 * @Date:   2024-08-20
 * @Version: V1.0
 */
public interface AhDeviceMapper extends BaseMapper<AhDevice> {

    /**
     * 自定义分页查询，关联牲畜信息
     *
     * @param page 分页参数
     * @param queryWrapper 查询条件
     * @return IPage<AhDevicePage>
     */
    IPage<AhDevicePage> getDevicePage(Page<AhDevicePage> page, @Param("ew") QueryWrapper<AhDevice> queryWrapper);


    /**
     * 获取设备监控仪表盘顶部的核心指标
     * @param offlineThresholdHours 离线小时数阈值
     * @param lowBatteryThreshold 低电量百分比阈值
     * @return DeviceKpiVo
     */
    DeviceKpiVo getDeviceKpis(@Param("offlineThresholdHours") int offlineThresholdHours, @Param("lowBatteryThreshold") int lowBatteryThreshold);

    /**
     * 分页查询所有“有问题”的设备列表
     * @param page 分页参数
     * @param offlineThresholdHours 离线小时数阈值
     * @param lowBatteryThreshold 低电量百分比阈值
     * @return IPage<ProblematicDeviceVo>
     */
    IPage<ProblematicDeviceVo> queryProblematicDevices(Page<ProblematicDeviceVo> page, @Param("offlineThresholdHours") int offlineThresholdHours, @Param("lowBatteryThreshold") int lowBatteryThreshold);
} 