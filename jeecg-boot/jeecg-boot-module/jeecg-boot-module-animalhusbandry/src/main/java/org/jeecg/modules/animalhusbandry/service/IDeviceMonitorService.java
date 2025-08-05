package org.jeecg.modules.animalhusbandry.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.animalhusbandry.dto.MaintenanceTaskDto;
import org.jeecg.modules.animalhusbandry.dto.RpcCommandDto;
import org.jeecg.modules.animalhusbandry.dto.TelemetryHistoryQueryDto;
import org.jeecg.modules.animalhusbandry.vo.DeviceKpiVo;
import org.jeecg.modules.animalhusbandry.vo.ProblematicDeviceVo;
import org.jeecg.common.api.vo.Result;

import java.util.List;
import java.util.Map;

/**
 * @Description: 设备监控服务接口
 * @Author: Gemini
 * @Date:   2024-08-23
 * @Version: V1.0
 */
public interface IDeviceMonitorService {

    /**
     * 获取设备监控仪表盘顶部的核心指标
     * @return
     */
    DeviceKpiVo getKpis();

    /**
     * 分页查询所有“有问题”的设备列表
     * @param page 分页对象
     * @return
     */
    IPage<ProblematicDeviceVo> queryProblematicDevices(Page<ProblematicDeviceVo> page);

    /**
     * 获取单个设备指定key的历史遥测数据
     * @param queryDto 查询参数
     * @return
     */
    Map<String, List<Map<String, Object>>> getTelemetryHistory(TelemetryHistoryQueryDto queryDto);

    /**
     * 向指定设备发送一个远程指令 (RPC)
     * @param commandDto 指令参数
     * @return
     */
    Result<?> sendRpcCommand(RpcCommandDto commandDto);

    /**
     * 为设备创建维保任务
     * @param taskDto 任务参数
     * @return
     */
    Result<?> createMaintenanceTask(MaintenanceTaskDto taskDto);
} 