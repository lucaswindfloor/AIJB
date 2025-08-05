package org.jeecg.modules.animalhusbandry.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Description: TDengine时序数据服务实现
 * @Author: AI Assistant
 * @Date: 2025-01-15
 * @Version: V1.0
 */
@Service
@Slf4j
public class TDengineTimeSeriesServiceImpl {

    @Autowired
    @Qualifier("tdengineJdbcTemplate")
    private JdbcTemplate tdengineJdbcTemplate;

    /**
     * 获取设备时序遥测数据
     * @param deviceId 设备ID (如: DEV-CAP-001)
     * @param keys 遥测键名，多个用逗号分隔 (如: Temperature,Gastric_momentum)
     * @param startTs 开始时间戳(毫秒)
     * @param endTs 结束时间戳(毫秒)
     * @return 按键名分组的时序数据
     */
    public Map<String, List<Map<String, Object>>> getTimeSeriesData(String deviceId, String keys, Long startTs, Long endTs) {
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        
        if (deviceId == null || keys == null || startTs == null || endTs == null) {
            log.warn("TDengine查询参数不完整: deviceId={}, keys={}, startTs={}, endTs={}", deviceId, keys, startTs, endTs);
            return result;
        }

        try {
            // 分割遥测键名
            String[] keyArray = keys.split(",");
            
            for (String key : keyArray) {
                String trimmedKey = key.trim();
                List<Map<String, Object>> dataList = querySingleKey(deviceId, trimmedKey, startTs, endTs);
                result.put(trimmedKey, dataList);
            }
            
            log.info("TDengine查询成功: deviceId={}, keys={}, 返回数据项={}", deviceId, keys, result.size());
            return result;
            
        } catch (DataAccessException e) {
            log.error("TDengine查询失败: deviceId={}, keys={}, error={}", deviceId, keys, e.getMessage(), e);
            // 返回空结果而不是抛出异常，避免影响前端显示
            return result;
        }
    }

    /**
     * 查询单个遥测键的时序数据
     */
    private List<Map<String, Object>> querySingleKey(String deviceId, String key, Long startTs, Long endTs) {
        // 构建TDengine查询SQL
        String tableName = "telemetry_" + deviceId.toLowerCase().replace("-", "_");
        String sql = "SELECT ts, " + key + " FROM " + tableName + 
                    " WHERE ts >= ? AND ts <= ? AND " + key + " IS NOT NULL ORDER BY ts";
        
        log.debug("TDengine SQL: {}, params: [{}, {}]", sql, new Date(startTs), new Date(endTs));
        
        try {
            return tdengineJdbcTemplate.query(sql, new Object[]{new Date(startTs), new Date(endTs)}, 
                (rs, rowNum) -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("ts", rs.getTimestamp("ts").getTime());
                    item.put("value", rs.getObject(key));
                    return item;
                });
        } catch (Exception e) {
            log.error("查询TDengine表 {} 的 {} 字段失败: {}", tableName, key, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 获取设备最新的遥测快照
     */
    public Map<String, Object> getLatestTelemetry(String deviceId) {
        String tableName = "telemetry_" + deviceId.toLowerCase().replace("-", "_");
        String sql = "SELECT last(*) FROM " + tableName;
        
        try {
            List<Map<String, Object>> results = tdengineJdbcTemplate.queryForList(sql);
            return results.isEmpty() ? Collections.emptyMap() : results.get(0);
        } catch (Exception e) {
            log.error("获取设备 {} 最新遥测数据失败: {}", deviceId, e.getMessage());
            return Collections.emptyMap();
        }
    }

    /**
     * 测试TDengine连接
     */
    public boolean testConnection() {
        try {
            tdengineJdbcTemplate.queryForObject("SELECT SERVER_VERSION()", String.class);
            log.info("TDengine连接测试成功");
            return true;
        } catch (Exception e) {
            log.error("TDengine连接测试失败: {}", e.getMessage());
            return false;
        }
    }
} 