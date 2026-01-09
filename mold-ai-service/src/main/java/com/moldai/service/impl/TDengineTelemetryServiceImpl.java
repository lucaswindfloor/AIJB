package com.moldai.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moldai.algorithm.VTTCalculator;
import com.moldai.service.ITelemetryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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
    private DataSource dataSource; // 直接注入 DataSource，不再用 JdbcTemplate

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<VTTCalculator.DataPoint> queryTelemetry(String deviceId, long startTs, long endTs, String interval) {
        String tableName;
        String cleanId = deviceId.replaceAll("[^a-zA-Z0-9_]", ""); 
        if (cleanId.startsWith("dev_")) {
            tableName = cleanId;
        } else {
            tableName = "dev_" + cleanId;
        }
        
        // 加上库名
        String sql = String.format("SELECT ts, raw_data FROM animal_husbandry.%s WHERE ts >= %d AND ts <= %d", 
                                   tableName, startTs, endTs);
        
        log.info("Executing TDengine SQL (Native JDBC): {}", sql);
        
        List<VTTCalculator.DataPoint> rawPoints = new ArrayList<>();
        
        // 使用 try-with-resources 自动关闭连接
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                try {
                    long ts = rs.getTimestamp("ts").getTime();
                    String rawJson = rs.getString("raw_data");
                    
                    if (rawJson == null || rawJson.isEmpty()) {
                        continue;
                    }
                    
                    JsonNode root = objectMapper.readTree(rawJson);
                    Double temp = findValueRobust(root, "temperature");
                    Double hum = findValueRobust(root, "relativeHumidity");
                    if (hum == null) {
                        hum = findValueRobust(root, "humidity");
                    }
                    
                    if (temp != null && hum != null) {
                        rawPoints.add(new VTTCalculator.DataPoint(ts, temp, hum));
                    }
                } catch (Exception e) {
                    // ignore single row parsing error
                }
            }
            
            log.info("Found {} raw data points", rawPoints.size());
            
            if (interval != null && !rawPoints.isEmpty()) {
                return aggregatePoints(rawPoints, interval);
            }
            
            return rawPoints;

        } catch (Exception e) {
            log.error("Error querying TDengine table [{}]. Error: {}", tableName, e.getMessage());
            e.printStackTrace(); // 打印完整堆栈
            return Collections.emptyList();
        }
    }

    @Override
    public void pushResult(String deviceId, VTTCalculator.RiskResult result) {
        log.info("Result for device {}: MI={}, Level={}", deviceId, result.getMiValue(), result.getRiskLevel());
    }

    private List<VTTCalculator.DataPoint> aggregatePoints(List<VTTCalculator.DataPoint> rawPoints, String interval) {
        if (!"1h".equals(interval)) {
            return rawPoints;
        }

        Map<Long, List<VTTCalculator.DataPoint>> grouped = rawPoints.stream()
            .collect(Collectors.groupingBy(p -> {
                return p.getTimestamp() / (3600 * 1000) * (3600 * 1000);
            }));
            
        return grouped.entrySet().stream()
            .map(entry -> {
                long hourTs = entry.getKey();
                List<VTTCalculator.DataPoint> points = entry.getValue();
                
                double avgTemp = points.stream().mapToDouble(VTTCalculator.DataPoint::getTemperature).average().orElse(0.0);
                double avgHum = points.stream().mapToDouble(VTTCalculator.DataPoint::getHumidity).average().orElse(0.0);
                
                return new VTTCalculator.DataPoint(hourTs, avgTemp, avgHum);
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
