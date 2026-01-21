package com.moldai.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moldai.algorithm.VTTCalculator;
import com.moldai.service.ITelemetryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TDengineTelemetryServiceImpl implements ITelemetryService {

    @Autowired
    @Qualifier("tdengineDataSource")
    private DataSource dataSource;

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private final WebClient webClient;

    @Value("${mold-ai.push.enabled:false}")
    private boolean pushEnabled;

    @Value("${mold-ai.push.target-url:}")
    private String pushTargetUrl;

    public TDengineTelemetryServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public List<VTTCalculator.DataPoint> queryTelemetry(String deviceId, long startTs, long endTs, String interval) {
        String tableName;
        String cleanId = deviceId.replaceAll("[^a-zA-Z0-9_]", ""); 
        if (cleanId.startsWith("dev_")) {
            tableName = cleanId;
        } else {
            tableName = "dev_" + cleanId;
        }
        
        String sql = String.format("SELECT ts, raw_data FROM animal_husbandry.%s WHERE ts >= %d AND ts <= %d", 
                                   tableName, startTs, endTs);
        
        log.debug("Executing TDengine SQL: {}", sql);
        
        List<VTTCalculator.DataPoint> rawPoints = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                try {
                    long ts = rs.getTimestamp("ts").getTime();
                    String rawJson = rs.getString("raw_data");
                    
                    if (rawJson == null || rawJson.isEmpty()) continue;
                    
                    JsonNode root = objectMapper.readTree(rawJson);
                    Double temp = findValueRobust(root, "temperature");
                    Double hum = findValueRobust(root, "relativeHumidity");
                    if (hum == null) hum = findValueRobust(root, "humidity");
                    
                    if (temp != null && hum != null) {
                        rawPoints.add(new VTTCalculator.DataPoint(ts, temp, hum));
                    }
                } catch (Exception e) {
                    // ignore single row error
                }
            }
            
            log.info("Found {} raw data points for device {}", rawPoints.size(), deviceId);
            
            if (interval != null && !rawPoints.isEmpty()) {
                return aggregatePoints(rawPoints, interval);
            }
            return rawPoints;

        } catch (Exception e) {
            log.error("Error querying TDengine: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public void pushResult(String deviceId, VTTCalculator.RiskResult result) {
        log.info("Analysis Result - Device: {}, MI: {}, Level: {}", deviceId, result.getMiValue(), result.getRiskLevel());
        
        if (pushEnabled && pushTargetUrl != null && !pushTargetUrl.isEmpty()) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("deviceId", deviceId);
            payload.put("miValue", result.getMiValue());
            payload.put("riskLevel", result.getRiskLevel());
            payload.put("timestamp", System.currentTimeMillis());

            webClient.post()
                .uri(pushTargetUrl)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(res -> log.info("Push success: {}", res))
                .doOnError(err -> log.error("Push failed: {}", err.getMessage()))
                .subscribe();
        }
    }

    private List<VTTCalculator.DataPoint> aggregatePoints(List<VTTCalculator.DataPoint> rawPoints, String interval) {
        long intervalMs;
        switch (interval) {
            case "30m": intervalMs = 30 * 60 * 1000L; break;
            case "1h": intervalMs = 60 * 60 * 1000L; break;
            case "15m": intervalMs = 15 * 60 * 1000L; break;
            default: return rawPoints;
        }

        Map<Long, List<VTTCalculator.DataPoint>> grouped = rawPoints.stream()
            .collect(Collectors.groupingBy(p -> p.getTimestamp() / intervalMs * intervalMs));
            
        return grouped.entrySet().stream()
            .map(entry -> {
                long bucketTs = entry.getKey();
                List<VTTCalculator.DataPoint> points = entry.getValue();
                double avgTemp = points.stream().mapToDouble(VTTCalculator.DataPoint::getTemperature).average().orElse(0.0);
                double avgHum = points.stream().mapToDouble(VTTCalculator.DataPoint::getHumidity).average().orElse(0.0);
                return new VTTCalculator.DataPoint(bucketTs, avgTemp, avgHum);
            })
            .sorted(Comparator.comparing(VTTCalculator.DataPoint::getTimestamp))
            .collect(Collectors.toList());
    }

    private Double findValueRobust(JsonNode root, String key) {
        JsonNode node = findValueCaseInsensitive(root, key);
        if (node == null) {
            JsonNode dataNode = root.path("data");
            if (!dataNode.isMissingNode()) {
                node = findValueCaseInsensitive(dataNode, key);
            }
        }
        if (node != null) {
            if (node.isNumber()) return node.asDouble();
            if (node.isTextual()) {
                try { return Double.parseDouble(node.asText()); } catch (Exception e) {}
            }
        }
        return null;
    }

    private JsonNode findValueCaseInsensitive(JsonNode node, String key) {
        if (node == null || !node.isObject()) return null;
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            if (entry.getKey().equalsIgnoreCase(key)) return entry.getValue();
        }
        return null;
    }
}
