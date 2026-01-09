package com.moldai.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    // 1. MySQL 主数据源配置
    // 使用 DataSourceProperties 来读取 spring.datasource 下的通用配置(url, username...)
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties mysqlDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "mysqlDataSource")
    @Primary
    public DataSource mysqlDataSource() {
        return mysqlDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    // 2. TDengine 副数据源配置
    // 手动注入，不依赖 DataSourceProperties 的自动检测，避免 "Suitable driver class" 报错
    @Bean(name = "tdengineDataSource")
    public DataSource tdengineDataSource(
            @Value("${mold-ai.tdengine.url}") String url,
            @Value("${mold-ai.tdengine.username}") String username,
            @Value("${mold-ai.tdengine.password}") String password) {
        
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setDriverClassName("com.taosdata.jdbc.TSDBDriver");
        return ds;
    }

    // 3. TDengine 专用的 JdbcTemplate
    @Bean(name = "tdengineJdbcTemplate")
    public JdbcTemplate tdengineJdbcTemplate(DataSource tdengineDataSource) {
        return new JdbcTemplate(tdengineDataSource);
    }
}
