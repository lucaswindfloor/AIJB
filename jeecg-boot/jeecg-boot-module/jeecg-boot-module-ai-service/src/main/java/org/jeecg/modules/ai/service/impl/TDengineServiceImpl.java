package org.jeecg.modules.ai.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.ai.service.ITDengineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @Description: TDengine数据服务实现类
 * @Author: AI Assistant
 * @Date:   2024-08-24
 * @Version: V1.0
 */
@Service
@Slf4j
public class TDengineServiceImpl implements ITDengineService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @DS("tdengine") // 关键：使用 @DS 注解切换到 tdengine 数据源
    public List<Map<String, Object>> queryPhysiologicalData(String deviceId, int hoursAgo) {
        // 注意：TDengine的超级表名和子表名（deviceId）需要区分大小写，请根据实际情况调整
        // 假设超级表名为 bolus_data, 字段名为 ts, temperature, ph_value, activity
        String sql = String.format(
            "SELECT ts, temperature, ph_value, activity " +
            "FROM bolus_data.`%s` " +
            "WHERE ts > NOW - %da",
            deviceId,
            hoursAgo
        );

        try {
            return jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            log.error("查询TDengine数据失败，设备ID: {}, SQL: {}", deviceId, sql, e);
            // 返回空集合，防止上游出现空指针异常
            return Collections.emptyList();
        }
    }
}
