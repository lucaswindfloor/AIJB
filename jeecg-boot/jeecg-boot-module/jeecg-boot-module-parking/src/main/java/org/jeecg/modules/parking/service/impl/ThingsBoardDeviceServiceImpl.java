package org.jeecg.modules.parking.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.parking.entity.LockDevice;
import org.jeecg.modules.parking.service.IThingsBoardDeviceService;
import org.springframework.stereotype.Service;

/**
 * @Description: 与ThingsBoard平台交互的服务实现
 * @Author: AI Assistant
 * @Date:   2025-07-01
 * @Version: V1.0
 */
@Service
@Slf4j
public class ThingsBoardDeviceServiceImpl implements IThingsBoardDeviceService {

    @Override
    public Result<?> sendControlCommand(LockDevice lockDevice, String command, String reason) {
        // 检查设备是否已在ThingsBoard注册
        if (lockDevice.getTbDeviceId() == null || lockDevice.getTbDeviceId().isEmpty()) {
            log.error("设备 '{}' (ID: {}) 未在ThingsBoard平台注册，无法发送命令。", lockDevice.getDeviceName(), lockDevice.getId());
            return Result.error("控制失败：设备未同步到物联网平台。");
        }

        // --- 模拟调用ThingsBoard RPC ---
        // 实际实现中，这里会使用RestTemplate或HttpClient调用ThingsBoard的REST API
        // API端点: /api/plugins/rpc/twoway/{deviceId}
        log.info("【模拟发送RPC】准备向ThingsBoard发送命令...");
        log.info("  - ThingsBoard设备ID: {}", lockDevice.getTbDeviceId());
        log.info("  - 命令: {}", command);
        log.info("  - 参数: { \"reason\": \"{}\" }", reason);

        // 模拟一个成功的响应
        log.info("【模拟发送RPC】命令已发送，等待设备响应...");
        
        // 在实际应用中，这里可能会异步处理来自设备的响应。
        // 目前，我们直接返回一个表示"命令已发送"的成功结果。
        return Result.OK("控制命令已发送成功，请稍后查看设备状态。");
    }
} 