package org.jeecg.modules.moldai.config;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@ConditionalOnProperty(name = "spring.datasource.dynamic.datasource.tdengine.url")
public class MoldAiTDengineConfig {

    @Bean("moldAiTdengineJdbcTemplate")
    public JdbcTemplate moldAiTdengineJdbcTemplate(DynamicRoutingDataSource dynamicRoutingDataSource) {
        try {
            DataSource tdengineDataSource = dynamicRoutingDataSource.getDataSource("tdengine");
            if (tdengineDataSource == null) {
                throw new RuntimeException("TDengine数据源未找到");
            }
            return new JdbcTemplate(tdengineDataSource);
        } catch (Exception e) {
            throw new RuntimeException("TDengine JdbcTemplate创建失败: " + e.getMessage(), e);
        }
    }
}


