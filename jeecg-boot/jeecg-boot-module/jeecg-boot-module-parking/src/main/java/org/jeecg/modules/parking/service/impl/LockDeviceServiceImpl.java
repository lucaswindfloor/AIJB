package org.jeecg.modules.parking.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.parking.config.ThingsboardConfig;
import org.jeecg.modules.parking.entity.LockDevice;
import org.jeecg.modules.parking.mapper.LockDeviceMapper;
import org.jeecg.modules.parking.service.ILockDeviceService;
import org.jeecg.modules.parking.service.IThingsboardAuthService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 车位锁设备
 * @Author: jeecg-boot
 * @Date:   2025-07-01
 * @Version: V1.0
 */
@Service
@Slf4j
public class LockDeviceServiceImpl extends ServiceImpl<LockDeviceMapper, LockDevice> implements ILockDeviceService {

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private IThingsboardAuthService thingsboardAuthService;

    @Resource
    private ThingsboardConfig thingsboardConfig;


    @Override
    public void controlLock(String deviceId, String action) {
        LockDevice device = this.getById(deviceId);
        if (device == null || device.getTbDeviceId() == null) {
            throw new JeecgBootException("设备未找到或未关联ThingsBoard！请检查 [tb_device_id] 字段。");
        }

        // 1. 拼接ThingsBoard双向RPC URL
        String url = thingsboardConfig.getHost() + "/api/rpc/twoway/" + device.getTbDeviceId();
        log.info("Sending Two-Way RPC to ThingsBoard: URL={}, Action={}", url, action);

        // 2. 设置HTTP Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Authorization", "Bearer " + thingsboardAuthService.getAccessToken());

        // 3. 构建请求体
        Map<String, String> params = new HashMap<>();
        if ("down".equals(action)) {
            params.put("lockControl", "03"); // 降锁，根据设备文档，使用APP降锁指令
        } else if ("up".equals(action)) {
            params.put("lockControl", "02"); // 升锁 (当前可用)
        } else {
            throw new JeecgBootException("不支持的控制指令: " + action);
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("method", "LockControl");
        requestBody.put("params", params);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // 4. 发送HTTP POST请求并处理响应
        try {
            String response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
            log.info("RPC for device {} with action {} sent successfully. Response: {}", device.getDeviceNo(), action, response);
        } catch (Exception e) {
            log.error("Failed to send RPC to ThingsBoard for device {}", device.getDeviceNo(), e);
            throw new JeecgBootException("发送控制指令到ThingsBoard失败: " + e.getMessage());
        }
    }

    @Override
    public void refreshStatus(String deviceId) {
        LockDevice device = this.getById(deviceId);
        if (device == null || device.getTbDeviceId() == null) {
            throw new JeecgBootException("设备未找到或未关联ThingsBoard！请检查 [tb_device_id] 字段。");
        }

        String url = thingsboardConfig.getHost() + "/api/rpc/twoway/" + device.getTbDeviceId();
        log.info("Sending Refresh RPC to ThingsBoard: URL={}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Authorization", "Bearer " + thingsboardAuthService.getAccessToken());

        // 构建请求体，方法修改为 LockAttrReading
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("method", "LockAttrReading");
        requestBody.put("params", new HashMap<>());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            // 1. 获取原始的JSON字符串响应
            String responseJson = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
            log.info("Raw RPC response for device {}: {}", device.getDeviceNo(), responseJson);

            // 2.【修复】处理双重编码的JSON：先将外层JSON字符串解析为普通字符串
            String actualJson = objectMapper.readValue(responseJson, String.class);
            log.info("Unwrapped JSON for device {}: {}", device.getDeviceNo(), actualJson);
            
            // 3. 将解包后的真实JSON字符串解析为Map
            Map<String, Object> telemetryData = objectMapper.readValue(actualJson, Map.class);

            // 4.【调试】为了确定返回的字段，我们先在这里打印解析后的数据
            log.info("Successfully parsed telemetry data for device {}: {}", device.getDeviceNo(), telemetryData.toString());

            /*
            // 5.【待定】根据日志中看到的真实字段名，我们再来完成这里的更新逻辑
            if (telemetryData.containsKey("lockStatus")) {
                device.setLockStatus(Integer.parseInt(telemetryData.get("lockStatus").toString()));
            }
            if (telemetryData.containsKey("isOccupied")) {
                device.setIsOccupied(Integer.parseInt(telemetryData.get("isOccupied").toString()));
            }
            if (telemetryData.containsKey("batteryLevel")) {
                device.setBatteryLevel(Integer.parseInt(telemetryData.get("batteryLevel").toString()));
            }
            if (telemetryData.containsKey("signalStrength")) {
                device.setSignalStrength(Integer.parseInt(telemetryData.get("signalStrength").toString()));
            }

            device.setLastHeartbeatTime(new Date());
            this.updateById(device);
            log.info("Device {} status updated successfully from ThingsBoard.", device.getDeviceNo());
            */

        } catch (Exception e) {
            log.error("Failed to refresh status from ThingsBoard for device {}", device.getDeviceNo(), e);
            throw new JeecgBootException("从ThingsBoard刷新状态失败: " + e.getMessage());
        }
    }
} 