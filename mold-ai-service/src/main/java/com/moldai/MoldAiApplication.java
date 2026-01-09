package com.moldai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.moldai.mapper")
public class MoldAiApplication {
    public static void main(String[] args) {
        SpringApplication.run(MoldAiApplication.class, args);
    }
}


