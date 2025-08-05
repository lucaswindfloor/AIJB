package org.jeecg.modules.animalhusbandry.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.animalhusbandry.dto.MaintenanceTaskDto;
import org.jeecg.modules.animalhusbandry.dto.RpcCommandDto;
import org.jeecg.modules.animalhusbandry.dto.TelemetryHistoryQueryDto;
import org.jeecg.modules.animalhusbandry.service.IDeviceMonitorService;
import org.jeecg.modules.animalhusbandry.vo.DeviceKpiVo;
import org.jeecg.modules.animalhusbandry.vo.ProblematicDeviceVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @Description: 设备监控
 * @Author: Gemini
 * @Date: 2024-08-23
 * @Version: V1.0
 */
@Tag(name = "设备监控")
@RestController
@RequestMapping("/animal_husbandry/deviceMonitor")
@Slf4j
public class DeviceMonitorController {

    @Autowired
    private IDeviceMonitorService deviceMonitorService;

    @Operation(summary = "获取设备监控KPI", description = "获取设备监控仪表盘顶部的核心指标（总数、在线、离线、低电量）")
    @GetMapping(value = "/kpis")
    public Result<DeviceKpiVo> getKpis() {
        DeviceKpiVo kpis = deviceMonitorService.getKpis();
        return Result.OK(kpis);
    }

    @Operation(summary = "查询有问题的设备列表", description = "分页查询所有“有问题”的设备列表（如离线>12小时或电量<20%）")
    @GetMapping(value = "/problematicList")
    public Result<IPage<ProblematicDeviceVo>> queryProblematicDevices(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<ProblematicDeviceVo> page = new Page<>(pageNo, pageSize);
        IPage<ProblematicDeviceVo> pageList = deviceMonitorService.queryProblematicDevices(page);
        return Result.OK(pageList);
    }

    @Operation(summary = "获取历史遥测数据", description = "获取单个设备指定key的历史遥测数据，用于绘制图表")
    @GetMapping(value = "/telemetryHistory")
    public Result<Map<String, List<Map<String, Object>>>> getTelemetryHistory(TelemetryHistoryQueryDto queryDto) {
        Map<String, List<Map<String, Object>>> historyData = deviceMonitorService.getTelemetryHistory(queryDto);
        return Result.OK(historyData);
    }

    @Operation(summary = "发送RPC指令", description = "向指定设备发送一个远程指令（RPC）")
    @PostMapping(value = "/rpc")
    public Result<?> sendRpcCommand(@RequestBody RpcCommandDto commandDto) {
        return deviceMonitorService.sendRpcCommand(commandDto);
    }

    @Operation(summary = "创建维保任务", description = "为设备创建维保任务，并将其状态变更为“维保中”")
    @PostMapping(value = "/createMaintenanceTask")
    public Result<?> createMaintenanceTask(@RequestBody MaintenanceTaskDto taskDto) {
        return deviceMonitorService.createMaintenanceTask(taskDto);
    }
} 