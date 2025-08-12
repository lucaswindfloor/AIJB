package org.jeecg.modules.animalhusbandry.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.animalhusbandry.service.impl.TDengineTimeSeriesServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TelemetryConsumerService {
    @Autowired
    private TDengineTimeSeriesServiceImpl tdengineService;
    @Autowired
    private ObjectMapper objectMapper;
    private static final String KAFKA_TOPIC = "telemetry_f0c794c0-e903-11ef-a8ee-99a8c68f9649";
    
    @KafkaListener(topics = KAFKA_TOPIC, groupId = "animal-husbandry-group")
    public void consume(String message) {
        log.info("从Kafka主题 '{}' 接收到消息: {}", KAFKA_TOPIC, message);
        try {
            // 1. 将消息解析为通用的JSON树，只为提取deviceName
            JsonNode rootNode = objectMapper.readTree(message);
            JsonNode deviceNameNode = rootNode.path("deviceName");

            if (deviceNameNode.isMissingNode() || !deviceNameNode.isTextual()) {
                log.warn("收到的Kafka消息中缺少有效的deviceName字段，已忽略。消息: {}", message);
                return;
            }
            
            String deviceName = deviceNameNode.asText();

            // 2. 调用服务，传入原始消息字符串进行存储
            tdengineService.saveRawTelemetryData(deviceName, message);

        } catch (JsonProcessingException e) {
            log.error("解析Kafka消息以提取deviceName失败: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("处理遥测数据时发生未知错误: {}", e.getMessage(), e);
        }
    }
}