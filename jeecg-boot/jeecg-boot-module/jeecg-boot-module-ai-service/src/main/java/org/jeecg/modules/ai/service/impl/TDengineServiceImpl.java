package org.jeecg.modules.ai.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.ai.service.ITDengineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(propagation = Propagation.NOT_SUPPORTED) // 关键：挂起当前事务，允许数据源切换
    public List<Map<String, Object>> queryPhysiologicalData(String devEui, int hoursAgo) {
        // 关键修正：
        // 1. 参数从 deviceId 改为 devEui，更准确
        // 2. 表名使用 dev_ + devEui 拼接
        // 3. 查询列改为 raw_data
        String sql = String.format(
            "SELECT ts, raw_data " +
            "FROM `dev_%s` " +
            "WHERE ts > NOW - %s", // 使用 %s 灵活配置时间范围
            devEui,
            "30d" // 临时将查询范围从 24a (24小时) 扩大到 30d (30天) 以便捞取历史测试数据
        );

        try {
            return jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            log.error("查询TDengine数据失败，设备EUI: {}, SQL: {}", devEui, sql, e);
            // 返回空集合，防止上游出现空指针异常
            return Collections.emptyList();
        }
    }
}
