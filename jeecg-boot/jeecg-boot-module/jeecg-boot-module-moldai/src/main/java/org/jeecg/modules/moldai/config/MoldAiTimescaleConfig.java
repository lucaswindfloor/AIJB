package org.jeecg.modules.moldai.config;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@ConditionalOnProperty(name = "spring.datasource.dynamic.datasource.timescale.url")
public class MoldAiTimescaleConfig {

    @Bean("moldAiTimescaleJdbcTemplate")
    public JdbcTemplate moldAiTimescaleJdbcTemplate(DynamicRoutingDataSource dynamicRoutingDataSource) {
        try {
            DataSource timescaleDataSource = dynamicRoutingDataSource.getDataSource("timescale");
            if (timescaleDataSource == null) {
                throw new RuntimeException("TimescaleDB数据源未找到");
            }
            return new JdbcTemplate(timescaleDataSource);
        } catch (Exception e) {
            throw new RuntimeException("TimescaleDB JdbcTemplate创建失败: " + e.getMessage(), e);
        }
    }
}


