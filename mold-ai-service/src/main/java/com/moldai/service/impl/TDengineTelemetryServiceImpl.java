package com.moldai.service.impl;

import com.moldai.algorithm.VTTCalculator;
import com.moldai.service.ITelemetryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class TDengineTelemetryServiceImpl implements ITelemetryService {

    @Autowired
    @Qualifier("tdengineJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<VTTCalculator.DataPoint> queryTelemetry(String deviceId, long startTs, long endTs, String interval) {
        // 这是一个示例 SQL，具体表结构需要根据 ThingsBoard 或 自研平台 的 TDengine 表结构调整
        // 假设表名为 device_telemetry，超级表为 meters
        // 实际使用时，表名通常是 "d_" + deviceId
        // 聚合函数：AVG(temperature), AVG(humidity)
        
        // 注意：这里为了演示，使用硬编码的表名规则，实际项目需配置
        String tableName = "d_" + deviceId.replace("-", "_"); // TDengine表名通常不含减号
        
        String sql = String.format(
            "SELECT _wstart as ts, AVG(temperature) as avg_temp, AVG(humidity) as avg_hum " +
            "FROM %s " +
            "WHERE ts >= %d AND ts < %d " +
            "INTERVAL(%s)", 
            tableName, startTs, endTs, interval
        );

        log.debug("Executing TDengine SQL: {}", sql);

        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                Timestamp ts = rs.getTimestamp("ts");
                double temp = rs.getDouble("avg_temp");
                double hum = rs.getDouble("avg_hum");
                return new VTTCalculator.DataPoint(ts.getTime(), temp, hum);
            });
        } catch (Exception e) {
            log.error("Error querying TDengine for device {}: {}", deviceId, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public void pushResult(String deviceId, VTTCalculator.RiskResult result) {
        log.info("Pushing result for device {}: MI={}, Level={}", 
                 deviceId, result.getMiValue(), result.getRiskLevel());
        // 如果需要回写到 TDengine，可以在这里实现 INSERT 语句
        // 或者推送到 MQTT
    }
}


