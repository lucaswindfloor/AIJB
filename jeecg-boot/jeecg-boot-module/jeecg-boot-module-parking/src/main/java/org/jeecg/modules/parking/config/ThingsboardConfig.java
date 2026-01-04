package org.jeecg.modules.parking.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * ThingsBoard 配置属性
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "thingsboard")
public class ThingsboardConfig {

    /**
     * ThingsBoard 服务器地址
     */
    private String host;

    /**
     * ThingsBoard 用户名 (e.g., tenant@hkt.com)
     */
    private String username;

    /**
     * ThingsBoard 密码
     */
    private String password;
} 