package org.jeecg.modules.animalhusbandry.config;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * @Description: TDengine配置类
 * @Author: AI Assistant
 * @Date: 2025-01-15
 * @Version: V1.0
 */
@Configuration
@ConditionalOnProperty(name = "spring.datasource.dynamic.datasource.tdengine.url")
public class TDengineConfig {

    /**
     * 创建TDengine专用的JdbcTemplate.
     *
     * 关键改动：
     * 我们不再使用@Autowired在字段上注入，而是将DynamicRoutingDataSource作为@Bean方法的参数。
     * 这样可以明确地告诉Spring：在调用此方法来创建'tdengineJdbcTemplate'之前，
     * 必须先去容器里找到并准备好一个DynamicRoutingDataSource的Bean。
     * 这就从根本上解决了Bean加载顺序的冲突问题。
     *
     * @param dynamicRoutingDataSource 由Spring Boot自动配置并在此处注入的动态数据源
     * @return TDengine的JdbcTemplate实例
     */
    @Bean("tdengineJdbcTemplate")
    public JdbcTemplate tdengineJdbcTemplate(DynamicRoutingDataSource dynamicRoutingDataSource) {
        try {
            // 从dynamic-datasource中获取指定的数据源
            DataSource tdengineDataSource = dynamicRoutingDataSource.getDataSource("tdengine");
            if (tdengineDataSource == null) {
                // 这个异常现在几乎不可能触发，除非Nacos配置错误
                throw new RuntimeException("TDengine数据源未找到，请检查Nacos配置：spring.datasource.dynamic.datasource.tdengine");
            }
            return new JdbcTemplate(tdengineDataSource);
        } catch (Exception e) {
            throw new RuntimeException("TDengine JdbcTemplate创建失败: " + e.getMessage(), e);
        }
    }
} 