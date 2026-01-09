package org.jeecg.modules.moldai.platform;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.moldai.algorithm.VTTCalculator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Component
public class ThingsBoardClient {

    @Value("${mold-ai.platform.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${mold-ai.platform.username:tenant@thingsboard.org}")
    private String username;

    @Value("${mold-ai.platform.password:tenant}")
    private String password;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private String token;
    private long tokenExpireTime = 0;

    /**
     * 获取设备历史遥测数据
     */
    public List<VTTCalculator.DataPoint> queryTelemetry(String deviceId, long startTs, long endTs) {
        ensureToken();
        
        // TB API: /api/plugins/telemetry/DEVICE/{deviceId}/values/timeseries
        String url = String.format("%s/api/plugins/telemetry/DEVICE/%s/values/timeseries?keys=temperature,humidity&startTs=%d&endTs=%d",
                baseUrl, deviceId, startTs, endTs);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return parseTelemetry(response.getBody());
            }
        } catch (Exception e) {
            log.error("获取设备[{}]遥测数据失败: {}", deviceId, e.getMessage());
        }
        return Collections.emptyList();
    }

    /**
     * 推送分析结果到设备遥测
     */
    public void pushResult(String deviceId, VTTCalculator.RiskResult result) {
        ensureToken();
        
        // TB API: /api/plugins/telemetry/DEVICE/{deviceId}/timeseries/ANY
        // 注意：TB通常通过MQTT上报，HTTP API通常是服务端API。
        // 为了简化，这里假设设备有一个关联的Access Token或者我们用服务端API saveEntityTelemetry
        String url = String.format("%s/api/plugins/telemetry/DEVICE/%s/timeseries/ANY", baseUrl, deviceId);

        Map<String, Object> telemetry = new HashMap<>();
        telemetry.put("mold_mi", result.getMiValue());
        telemetry.put("mold_risk_level", result.getRiskLevel());
        telemetry.put("mold_ts", System.currentTimeMillis());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(telemetry, headers);

        try {
            restTemplate.postForEntity(url, entity, String.class);
            log.info("推送设备[{}]风险结果成功: MI={}, Level={}", deviceId, result.getMiValue(), result.getRiskLevel());
        } catch (Exception e) {
            log.error("推送设备[{}]风险结果失败: {}", deviceId, e.getMessage());
        }
    }

    private void ensureToken() {
        if (System.currentTimeMillis() < tokenExpireTime) {
            return;
        }
        login();
    }

    private void login() {
        String url = baseUrl + "/api/auth/login";
        Map<String, String> request = new HashMap<>();
        request.put("username", username);
        request.put("password", password);

        try {
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(url, request, JsonNode.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                this.token = response.getBody().get("token").asText();
                // 简单的过期处理，实际应解析JWT
                this.tokenExpireTime = System.currentTimeMillis() + (1000 * 60 * 30); // 30 mins
            }
        } catch (Exception e) {
            log.error("ThingsBoard登录失败: {}", e.getMessage());
        }
    }

    private List<VTTCalculator.DataPoint> parseTelemetry(JsonNode body) {
        List<VTTCalculator.DataPoint> points = new ArrayList<>();
        JsonNode tempNode = body.get("temperature");
        JsonNode humNode = body.get("humidity");
        
        if (tempNode == null || humNode == null) return points;

        // 简单对齐逻辑：假设温湿度时间戳大致对应，或者按分钟对齐
        // 这里简化处理：以温度数据的时间戳为准，找最近的湿度
        // 生产环境需要更严谨的对齐算法
        for (JsonNode t : tempNode) {
            long ts = t.get("ts").asLong();
            double tempVal = t.get("value").asDouble();
            
            Double humVal = findClosestValue(humNode, ts);
            
            if (humVal != null) {
                points.add(new VTTCalculator.DataPoint(ts, tempVal, humVal));
            }
        }
        return points;
    }

    private Double findClosestValue(JsonNode dataArray, long targetTs) {
        double minDiff = Double.MAX_VALUE;
        Double closestVal = null;
        
        for (JsonNode d : dataArray) {
            long ts = d.get("ts").asLong();
            long diff = Math.abs(ts - targetTs);
            if (diff < minDiff) {
                minDiff = (double) diff;
                closestVal = d.get("value").asDouble();
            }
        }
        // 如果差异超过5分钟，视为无效
        if (minDiff > 5 * 60 * 1000) return null;
        return closestVal;
    }
}


