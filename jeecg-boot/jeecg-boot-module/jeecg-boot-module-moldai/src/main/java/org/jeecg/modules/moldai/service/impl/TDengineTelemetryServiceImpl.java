package org.jeecg.modules.moldai.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.moldai.algorithm.VTTCalculator;
import org.jeecg.modules.moldai.service.ITelemetryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("tdengineTelemetryService")
@ConditionalOnProperty(name = "mold-ai.platform.type", havingValue = "tdengine", matchIfMissing = true)
public class TDengineTelemetryServiceImpl implements ITelemetryService {

    @Autowired(required = false)
    @Qualifier("moldAiTdengineJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<VTTCalculator.DataPoint> queryTelemetry(String deviceId, long startTs, long endTs, String interval) {
        if (jdbcTemplate == null) {
            log.error("TDengine JdbcTemplate未初始化，无法查询");
            return Collections.emptyList();
        }

        // 智能表名处理：防止重复添加 dev_ 前缀
        String tableName;
        String cleanId = deviceId.replaceAll("[^a-zA-Z0-9_]", ""); // 清理特殊字符
        if (cleanId.startsWith("dev_")) {
            tableName = cleanId; // 已经是 dev_ 开头，直接用
        } else {
            tableName = "dev_" + cleanId; // 补上 dev_ 前缀
        }
        
        // 查询 raw_data
        String sql = "SELECT ts, raw_data FROM " + tableName + " WHERE ts >= ? AND ts <= ?";
        
        try {
            List<VTTCalculator.DataPoint> rawPoints = new ArrayList<>();
            
            // 使用 java.util.Date 兼容性更好
            jdbcTemplate.query(sql, new Object[]{new Date(startTs), new Date(endTs)}, (rs) -> {
                try {
                    long ts = rs.getTimestamp("ts").getTime();
                    String rawJson = rs.getString("raw_data");
                    
                    // 解析 JSON
                    JsonNode root = objectMapper.readTree(rawJson);
                    
                    // 采用更健壮的解析逻辑：不区分大小写，且尝试从 data 子节点查找
                    Double temp = findValueRobust(root, "temperature");
                    Double hum = findValueRobust(root, "relativeHumidity"); // 兼容 relativeHumidity
                    if (hum == null) {
                        hum = findValueRobust(root, "humidity");
                    }
                    
                    if (temp != null && hum != null) {
                        rawPoints.add(new VTTCalculator.DataPoint(ts, temp, hum));
                    }
                } catch (Exception e) {
                    // 降低日志级别，避免大量刷屏
                    // log.warn("解析TDengine数据行失败: {}", e.getMessage());
                }
            });
            
            // 如果需要聚合
            if (interval != null && !rawPoints.isEmpty()) {
                return aggregatePoints(rawPoints, interval);
            }
            
            return rawPoints;
        } catch (Exception e) {
            log.error("查询TDengine失败: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public void pushResult(String deviceId, VTTCalculator.RiskResult result) {
        log.info("TDengine适配器暂不支持直接回写结果，建议通过 MySQL 存储结果");
    }

    /**
     * Java层聚合逻辑
     * 目前只支持按小时聚合 ("1h")
     */
    private List<VTTCalculator.DataPoint> aggregatePoints(List<VTTCalculator.DataPoint> rawPoints, String interval) {
        if (!"1h".equals(interval)) {
            log.warn("目前聚合只支持 '1h'，直接返回原始数据");
            return rawPoints;
        }

        Map<Long, List<VTTCalculator.DataPoint>> grouped = rawPoints.stream()
            .collect(Collectors.groupingBy(p -> {
                // 向下取整到小时
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

    /**
     * 健壮的数值查找逻辑
     * 1. 不区分大小写
     * 2. 尝试从 root 查找
     * 3. 尝试从 root.data 查找
     */
    private Double findValueRobust(JsonNode root, String key) {
        // 1. 尝试直接从 root 查找 (不区分大小写)
        JsonNode node = findValueCaseInsensitive(root, key);
        
        // 2. 如果没找到，尝试从 data 子节点查找
        if (node == null) {
            JsonNode dataNode = root.path("data");
            if (!dataNode.isMissingNode()) {
                node = findValueCaseInsensitive(dataNode, key);
            }
        }
        
        if (node != null && node.isNumber()) {
            return node.asDouble();
        }
        // 支持字符串类型的数字
        if (node != null && node.isTextual()) {
            try {
                return Double.parseDouble(node.asText());
            } catch (NumberFormatException ignored) {}
        }
        
        return null;
    }

    private JsonNode findValueCaseInsensitive(JsonNode node, String key) {
        if (node == null || key == null || !node.isObject()) {
            return null;
        }
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            if (entry.getKey().equalsIgnoreCase(key)) {
                return entry.getValue();
            }
        }
        return null;
    }
}
