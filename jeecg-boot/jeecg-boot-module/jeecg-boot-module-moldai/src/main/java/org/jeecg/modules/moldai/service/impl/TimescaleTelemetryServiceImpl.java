package org.jeecg.modules.moldai.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.moldai.algorithm.VTTCalculator;
import org.jeecg.modules.moldai.service.ITelemetryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service("timescaleTelemetryService")
@ConditionalOnProperty(name = "mold-ai.platform.type", havingValue = "timescale")
public class TimescaleTelemetryServiceImpl implements ITelemetryService {

    @Autowired(required = false)
    @Qualifier("moldAiTimescaleJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    private static final String TABLE_NAME = "mold_telemetry";

    @Override
    public List<VTTCalculator.DataPoint> queryTelemetry(String deviceId, long startTs, long endTs, String interval) {
        if (jdbcTemplate == null) {
            log.error("TimescaleDB JdbcTemplate未初始化，无法查询");
            return Collections.emptyList();
        }

        String sql;
        if ("1h".equals(interval)) {
            // 使用 PostgreSQL/TimescaleDB 的 time_bucket 进行聚合
            sql = "SELECT time_bucket('1 hour', ts) AS bucket, AVG(temperature) as temp, AVG(humidity) as hum " +
                  "FROM " + TABLE_NAME + 
                  " WHERE device_id = ? AND ts >= ? AND ts <= ? " +
                  "GROUP BY bucket ORDER BY bucket ASC";
        } else {
            sql = "SELECT ts as bucket, temperature as temp, humidity as hum FROM " + TABLE_NAME + 
                  " WHERE device_id = ? AND ts >= ? AND ts <= ? ORDER BY ts ASC";
        }
        
        try {
            List<VTTCalculator.DataPoint> points = new ArrayList<>();
            
            jdbcTemplate.query(sql, new Object[]{
                deviceId, 
                new Timestamp(startTs), 
                new Timestamp(endTs)
            }, (rs) -> {
                try {
                    long ts = rs.getTimestamp("bucket").getTime();
                    double temp = rs.getDouble("temp");
                    double hum = rs.getDouble("hum");
                    
                    if (!rs.wasNull()) {
                        points.add(new VTTCalculator.DataPoint(ts, temp, hum));
                    }
                } catch (Exception e) {
                    log.warn("解析TimescaleDB数据行失败: {}", e.getMessage());
                }
            });
            
            return points;
        } catch (Exception e) {
            log.error("查询TimescaleDB失败: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public void pushResult(String deviceId, VTTCalculator.RiskResult result) {
        if (jdbcTemplate == null) return;

        String sql = "INSERT INTO mold_risk_log (ts, device_id, mi, level) VALUES (?, ?, ?, ?)";
        try {
            jdbcTemplate.update(sql, 
                new Timestamp(System.currentTimeMillis()),
                deviceId,
                result.getMiValue(),
                result.getRiskLevel()
            );
            log.info("成功将风险结果写入TimescaleDB: device={}, mi={}", deviceId, result.getMiValue());
        } catch (Exception e) {
            log.error("写入TimescaleDB失败: {}", e.getMessage());
        }
    }
}
