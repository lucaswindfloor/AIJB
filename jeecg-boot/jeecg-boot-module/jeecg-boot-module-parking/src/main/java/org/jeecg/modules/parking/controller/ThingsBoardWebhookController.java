package org.jeecg.modules.parking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.parking.service.ILockDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Description: 接收并处理来自ThingsBoard的Webhook推送
 * @Author: AI Assistant
 * @Date: 2025-07-01
 * @Version: V1.0
 */
@Tag(name="ThingsBoard Webhook接收器")
@RestController
@RequestMapping("/parking/device/thingsboard")
@Slf4j
public class ThingsBoardWebhookController {

    @Autowired
    private ILockDeviceService lockDeviceService;

    /**
     * Webhook核心接口，用于接收遥测数据、设备事件等
     * @param webhookData 从ThingsBoard接收到的JSON数据
     * @return 处理结果
     */
    @Operation(summary="接收ThingsBoard Webhook数据", description="处理来自ThingsBoard的设备遥测和事件数据")
    @PostMapping("/webhook")
    public Result<?> receiveWebhook(@RequestBody Map<String, Object> webhookData) {
        log.info("【Webhook】收到来自ThingsBoard的推送数据: {}", webhookData);

        // --- 数据解析和处理 ---
        // 在实际场景中，这里会定义一个DTO来接收数据，并根据事件类型进行不同处理
        // 例如：POST_TELEMETRY, ACTIVITY_EVENT, RPC_CALL_FROM_SERVER_TO_DEVICE 等
        
        // 简化处理：假设数据中直接包含deviceName和遥测数据
        String deviceName = (String) webhookData.get("deviceName");
        if (deviceName == null || deviceName.isEmpty()) {
            log.error("【Webhook】处理失败：推送数据中缺少'deviceName'字段。");
            return Result.error("数据格式错误，缺少deviceName");
        }

        Map<String, Object> telemetry = (Map<String, Object>) webhookData.get("telemetry");
        if (telemetry == null || telemetry.isEmpty()){
            log.warn("【Webhook】警告：推送数据中不包含遥测(telemetry)信息。");
            // 即使没有遥测，也可能是一个在线/离线事件，先返回成功
            return Result.OK("Webhook已接收，但无遥测数据处理。");
        }

        // TODO: 根据deviceName（或更可靠的tbDeviceId）从数据库查找设备
        log.info("【Webhook】正在处理设备 '{}' 的遥测数据...", deviceName);
        log.info("  - 遥测数据: {}", telemetry);
        log.info("  - TODO: 更新数据库中对应设备的状态...");

        return Result.OK("Webhook数据已成功接收并处理。");
    }
} 