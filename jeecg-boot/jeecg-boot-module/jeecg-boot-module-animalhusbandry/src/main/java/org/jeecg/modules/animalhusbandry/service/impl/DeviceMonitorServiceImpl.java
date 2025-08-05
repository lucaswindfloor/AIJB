package org.jeecg.modules.animalhusbandry.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.animalhusbandry.entity.AhDevice;
import org.jeecg.modules.animalhusbandry.service.IAhDeviceService;
import org.jeecg.modules.animalhusbandry.dto.MaintenanceTaskDto;
import org.jeecg.modules.animalhusbandry.dto.RpcCommandDto;
import org.jeecg.modules.animalhusbandry.dto.TelemetryHistoryQueryDto;
import org.jeecg.modules.animalhusbandry.mapper.AhDeviceMapper;
import org.jeecg.modules.animalhusbandry.service.IDeviceMonitorService;
import org.jeecg.modules.animalhusbandry.service.IThingsBoardService;
import org.jeecg.modules.animalhusbandry.vo.DeviceKpiVo;
import org.jeecg.modules.animalhusbandry.vo.ProblematicDeviceVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class DeviceMonitorServiceImpl implements IDeviceMonitorService {

    // 从设计文档中获取阈值
    private static final int OFFLINE_THRESHOLD_HOURS = 12;
    private static final int LOW_BATTERY_THRESHOLD = 20;

    @Autowired
    private AhDeviceMapper ahDeviceMapper;

    @Autowired
    private IAhDeviceService deviceService;

    @Autowired
    private IThingsBoardService thingsBoardService;

    @Override
    public DeviceKpiVo getKpis() {
        return ahDeviceMapper.getDeviceKpis(OFFLINE_THRESHOLD_HOURS, LOW_BATTERY_THRESHOLD);
    }

    @Override
    public IPage<ProblematicDeviceVo> queryProblematicDevices(Page<ProblematicDeviceVo> page) {
        return ahDeviceMapper.queryProblematicDevices(page, OFFLINE_THRESHOLD_HOURS, LOW_BATTERY_THRESHOLD);
    }

    @Autowired
    private TDengineTimeSeriesServiceImpl tdengineService;

    @Override
    public Map<String, List<Map<String, Object>>> getTelemetryHistory(TelemetryHistoryQueryDto queryDto) {
        log.info("查询TDengine时序数据: deviceId={}, keys={}, startTs={}, endTs={}", 
                 queryDto.getDeviceId(), queryDto.getKeys(), queryDto.getStartTs(), queryDto.getEndTs());
        
        try {
            return tdengineService.getTimeSeriesData(
                queryDto.getDeviceId(), 
                queryDto.getKeys(), 
                queryDto.getStartTs(), 
                queryDto.getEndTs()
            );
        } catch (Exception e) {
            log.error("查询TDengine时序数据失败", e);
            // 失败时返回空数据，避免前端显示错误
            return Collections.emptyMap();
        }
    }

    @Override
    public Result<?> sendRpcCommand(RpcCommandDto commandDto) {
        AhDevice device = deviceService.getById(commandDto.getDeviceId());
        if (device == null) {
            return Result.error("设备不存在");
        }
        if (!StringUtils.hasText(device.getTbDeviceId())) {
            return Result.error("该设备没有关联的ThingsBoard ID，无法发送指令");
        }
        return thingsBoardService.sendRpc(device.getTbDeviceId(), commandDto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> createMaintenanceTask(MaintenanceTaskDto taskDto) {
        AhDevice device = deviceService.getById(taskDto.getDeviceId());
        if (device == null) {
            return Result.error("设备不存在");
        }
        if (Objects.equals(device.getStatus(), "RETIRED")) {
            return Result.error("已报废的设备无法创建维保任务");
        }
        // TODO: 可以在此创建一条维保任务记录到新表 `ah_maintenance_task`

        device.setStatus("MAINTENANCE"); // 使用数据字典中定义的状态: 维保中
        boolean success = deviceService.updateById(device);
        if (success) {
            log.info("设备 {} 已成功置为维保中状态", device.getId());
            return Result.OK("创建维保任务成功");
        } else {
            return Result.error("更新设备状态失败");
        }
    }
} 