package org.jeecg;

import org.jeecg.common.base.BaseMap;
import org.jeecg.common.constant.GlobalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 智能畜牧微服务启动类
 * (启动类放在根包org.jeecg下，才能正确扫描所有依赖和组件)
 */
@SpringBootApplication
@EnableFeignClients
public class JeecgAnimalHusbandryApplication implements CommandLineRunner {

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    public static void main(String[] args) {
        SpringApplication.run(JeecgAnimalHusbandryApplication.class, args);
    }
    
    /**
     * 启动的时候，触发下gateway网关刷新
     */
    @Override
    public void run(String... args) {
        // gateway启动后，修改swagger服务名，需要刷新网关路由
        if (redisTemplate != null) {
            BaseMap params = new BaseMap();
            params.put(GlobalConstants.HANDLER_NAME, GlobalConstants.LODER_ROUDER_HANDLER);
            redisTemplate.convertAndSend(GlobalConstants.REDIS_TOPIC_NAME, params);
        }
    }
} 