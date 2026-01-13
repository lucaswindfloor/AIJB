package com.moldai.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moldai.service.IDeviceControlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * ThingsBoard 设备控制实现
 * 通过 HTTP API 调用 ThingsBoard 的服务端 RPC
 */
@Slf4j
@Service
public class ThingsBoardControlServiceImpl implements IDeviceControlService {

    @Value("${mold-ai.thingsboard.api-url:http://localhost:8080/api}")
    private String tbApiUrl;

    @Value("${mold-ai.thingsboard.username:tenant@thingsboard.org}")
    private String tbUsername;

    @Value("${mold-ai.thingsboard.password:tenant}")
    private String tbPassword;
    
    // 是否启用真实的 TB 控制，如果在开发环境可能只想打印日志
    @Value("${mold-ai.thingsboard.enabled:false}")
    private boolean tbEnabled;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private String cachedToken = null;
    private long tokenExpireTime = 0;

    @Override
    public boolean sendRpcCommand(String deviceId, String method, Object params) {
        log.info("Preparing to send RPC to device [{}]: method={}, params={}", deviceId, method, params);

        if (!tbEnabled) {
            log.info("ThingsBoard control is DISABLED in config. Skipping actual API call.");
            return true;
        }

        try {
            String token = getJwtToken();
            if (token == null) {
                log.error("Failed to obtain ThingsBoard JWT token.");
                return false;
            }

            // 1. 获取设备的真实 UUID (ThingsBoard Internal ID)
            String tbDeviceId = deviceId; // 默认认为传入的就是 UUID
            
            // 简单校验是否是 UUID 格式
            if (!deviceId.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")) {
                 log.info("Device ID '{}' is not a UUID, trying to resolve by name...", deviceId);
                 tbDeviceId = getTbDeviceIdByName(deviceId, token);
                 if (tbDeviceId == null) {
                    log.error("Failed to find ThingsBoard UUID for device name: {}", deviceId);
                    return false;
                 }
                 log.info("Resolved device name '{}' to TB UUID '{}'", deviceId, tbDeviceId);
            } else {
                 log.debug("Device ID '{}' is a UUID, using directly.", deviceId);
            }

            // 2. 发送 RPC
            // 默认为 oneway，但可以通过某些方式支持 twoway。目前 AI 模块主要用于下发指令。
            // 注意：如果实际设备需要 twoway，且我们这里只调 oneway，指令可能仍然能下发，但无法获取设备返回值。
            // 根据用户提供的 debug 信息，地锁控制似乎是 /rpc/twoway/
            // 我们通过一个简单的判断逻辑来支持：如果 method 包含 "LockControl" 或其他特定方法，切到 twoway
            String callType = "oneway";
            if ("LockControl".equals(method)) {
                callType = "twoway";
            }
            
            String url = String.format("%s/plugins/rpc/%s/%s", tbApiUrl, callType, tbDeviceId);
            log.info("Sending RPC to URL: {}", url); // 添加日志，打印完整 URL
            
            // 确保 params 不为 null，如果是 null 则初始化为空 Map，避免 400 错误
            if (params == null) {
                params = new HashMap<>();
            }

            Map<String, Object> body = new HashMap<>();
            body.put("method", method);
            body.put("params", params);
            
            log.debug("RPC Request Payload: {}", objectMapper.writeValueAsString(body));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            
            try {
                ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
                
                if (response.getStatusCode().is2xxSuccessful()) {
                    log.info("RPC command sent successfully.");
                    return true;
                } else {
                    log.error("RPC command failed with status: {}", response.getStatusCode());
                    return false;
                }
            } catch (org.springframework.web.client.HttpClientErrorException e) {
                if (e.getStatusCode() == HttpStatus.REQUEST_TIMEOUT) {
                    // 408 Timeout: 这通常意味着指令已发送到 TB，但 TB 在等待设备响应时超时
                    // 对于休眠设备（如 NB-IoT 地锁），这很常见。
                    // 我们可以认为这是一个"半成功"状态：云端已接收，只是端侧未及时反馈
                    log.warn("RPC command sent but timed out waiting for device response (408). This is normal for sleeping devices. Command: {}", method);
                    return true; // 视为发送成功，不中断流程
                }
                log.error("RPC command failed with status {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
                return false;
            } catch (org.springframework.web.client.HttpStatusCodeException e) {
                log.error("RPC command failed with status {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
                return false;
            }

        } catch (Exception e) {
            log.error("Exception sending RPC command: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 根据设备名称查询 ThingsBoard 设备 UUID
     */
    private String getTbDeviceIdByName(String deviceName, String token) {
        try {
            // API: /api/tenant/devices?textSearch={deviceName}
            // 注意：这是 Tenant 级别的搜索，需要 Tenant 权限
            String url = String.format("%s/tenant/devices?textSearch=%s&pageSize=10&page=0", tbApiUrl, deviceName);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                com.fasterxml.jackson.databind.JsonNode root = objectMapper.readTree(response.getBody());
                com.fasterxml.jackson.databind.JsonNode data = root.path("data");
                if (data.isArray()) {
                    for (com.fasterxml.jackson.databind.JsonNode device : data) {
                        String name = device.path("name").asText();
                        if (deviceName.equals(name)) {
                            return device.path("id").path("id").asText();
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Error searching for device UUID in ThingsBoard: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 获取 TB 认证 Token (简单的缓存实现)
     */
    private synchronized String getJwtToken() {
        if (cachedToken != null && System.currentTimeMillis() < tokenExpireTime) {
            return cachedToken;
        }

        try {
            String loginUrl = tbApiUrl + "/auth/login";
            Map<String, String> loginRequest = new HashMap<>();
            loginRequest.put("username", tbUsername);
            loginRequest.put("password", tbPassword);

            ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, loginRequest, String.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                com.fasterxml.jackson.databind.JsonNode root = objectMapper.readTree(response.getBody());
                cachedToken = root.path("token").asText();
                // 假设 token 有效期较长，简单缓存 1 小时
                tokenExpireTime = System.currentTimeMillis() + 3600 * 1000; 
                return cachedToken;
            }
        } catch (Exception e) {
            log.error("Failed to login to ThingsBoard: {}", e.getMessage());
        }
        return null;
    }
}


