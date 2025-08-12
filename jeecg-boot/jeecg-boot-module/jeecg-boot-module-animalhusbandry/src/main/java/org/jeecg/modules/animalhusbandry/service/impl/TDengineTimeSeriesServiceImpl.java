package org.jeecg.modules.animalhusbandry.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.animalhusbandry.entity.AhDevice;
import org.jeecg.modules.animalhusbandry.service.IAhDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TDengineTimeSeriesServiceImpl {

    private static final String STABLE_NAME = "telemetry_data";

    @Autowired
    @Qualifier("tdengineJdbcTemplate")
    private JdbcTemplate tdengineJdbcTemplate;

    @Autowired
    private IAhDeviceService ahDeviceService;

    @PostConstruct
    public void init() {
        createSuperTable();
    }

    private void createSuperTable() {
        try {
            String sql = "CREATE STABLE IF NOT EXISTS " + STABLE_NAME + " (" +
                         "ts TIMESTAMP, " +
                         "raw_data NCHAR(16374)) " +
                         "TAGS (device_name NCHAR(128))";
            tdengineJdbcTemplate.execute(sql);
            log.info("TDengine通用遥测超级表 '{}' 初始化成功或已存在。", STABLE_NAME);
        } catch (DataAccessException e) {
            log.error("TDengine通用遥测超级表 '{}' 初始化失败: {}", STABLE_NAME, e.getMessage(), e);
        }
    }
    
    public void saveRawTelemetryData(String deviceName, String message) {
        if (deviceName == null || deviceName.isEmpty() || message == null || message.isEmpty()) {
            log.warn("接收到无效的遥测数据，deviceName或message为空。");
            return;
        }

        String tableName = "dev_" + deviceName.replaceAll("[^a-zA-Z0-9_]", "");
        
        try {
            createSubTableIfNotExists(tableName, deviceName);
            String sql = String.format("INSERT INTO %s (ts, raw_data) VALUES (now, ?)", tableName);
            tdengineJdbcTemplate.update(sql, message);
            log.info("成功将设备 '{}' 的原始遥测数据插入到TDengine表 '{}'。", deviceName, tableName);
        } catch (DataAccessException e) {
            log.error("插入原始遥测数据到TDengine失败: deviceName={}, tableName={}, error={}", 
                      deviceName, tableName, e.getMessage(), e);
        }
    }

    private void createSubTableIfNotExists(String tableName, String deviceName) {
        try {
            String createSql = String.format("CREATE TABLE IF NOT EXISTS %s USING %s TAGS ('%s')",
                    tableName, STABLE_NAME, deviceName);
            tdengineJdbcTemplate.execute(createSql);
        } catch (DataAccessException e) {
            log.error("创建TDengine子表 '{}' 失败: {}", tableName, e.getMessage());
            throw e;
        }
    }

    public List<Map<String, Object>> getTelemetryHistoryForKey(String deviceId, String telemetryKey, Long startTs, Long endTs) {
        String devEui = getDevEuiFromBusinessId(deviceId);
        if (devEui == null) {
            log.warn("无法根据业务ID '{}' 找到对应的DevEUI，查询中止。", deviceId);
            return Collections.emptyList();
        }

        String tableName = "dev_" + devEui.replaceAll("[^a-zA-Z0-9_]", "");
        String sql = "SELECT ts, raw_data FROM " + tableName + " WHERE ts >= ? AND ts <= ?";

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return tdengineJdbcTemplate.query(sql, new Object[]{new Date(startTs), new Date(endTs)}, (rs, rowNum) -> {
                try {
                    String rawJson = rs.getString("raw_data");
                    JsonNode rootNode = objectMapper.readTree(rawJson);
                    
                    JsonNode valueNode = findValueCaseInsensitive(rootNode, telemetryKey);
                    if (valueNode == null || valueNode.isMissingNode()) {
                        JsonNode dataNode = rootNode.path("data");
                        if (!dataNode.isMissingNode()) {
                             valueNode = findValueCaseInsensitive(dataNode, telemetryKey);
                        }
                    }

                    if (valueNode != null && !valueNode.isMissingNode() && !valueNode.isNull()) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("ts", rs.getTimestamp("ts").getTime());
                        if (valueNode.isNumber()) {
                            item.put("value", valueNode.asDouble());
                        } else {
                            item.put("value", valueNode.asText());
                        }
                        return item;
                    }
                } catch (Exception e) {
                    log.error("解析TDengine中的JSON数据失败, row: {}, error: {}", rs.getString("raw_data"), e.getMessage());
                }
                return null;
            }).stream().filter(Objects::nonNull).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询设备 {} 的历史遥测数据失败: {}", deviceId, e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Map<String, Object>> getRawTelemetryLog(String deviceId, Long startTs, Long endTs) {
        String devEui = getDevEuiFromBusinessId(deviceId);
        if (devEui == null) {
            log.warn("无法根据业务ID '{}' 找到对应的DevEUI，查询中止。", deviceId);
            return Collections.emptyList();
        }
        
        String tableName = "dev_" + devEui.replaceAll("[^a-zA-Z0-9_]", "");
        String sql = "SELECT ts, raw_data FROM " + tableName + " WHERE ts >= ? AND ts <= ? ORDER BY ts DESC";

        try {
            return tdengineJdbcTemplate.query(sql, new Object[]{new Date(startTs), new Date(endTs)}, (rs, rowNum) -> {
                Map<String, Object> item = new HashMap<>();
                item.put("ts", rs.getTimestamp("ts").getTime());
                item.put("value", rs.getString("raw_data"));
                return item;
            });
        } catch (Exception e) {
            log.error("查询设备 {} (DevEUI: {}) 的原始遥测日志失败: {}", deviceId, devEui, e.getMessage());
            return Collections.emptyList();
        }
    }

    private String getDevEuiFromBusinessId(String deviceId) {
        if (deviceId == null) return null;
        try {
            AhDevice device = ahDeviceService.getById(deviceId);
            if (device != null) {
                return device.getDevEui();
            }
        } catch (Exception e) {
            log.error("根据业务ID '{}' 查询设备信息时出错: {}", deviceId, e.getMessage());
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