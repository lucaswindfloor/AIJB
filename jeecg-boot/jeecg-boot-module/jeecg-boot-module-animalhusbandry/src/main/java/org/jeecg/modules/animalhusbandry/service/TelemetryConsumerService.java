package org.jeecg.modules.animalhusbandry.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.animalhusbandry.entity.AhAnimal;
import org.jeecg.modules.animalhusbandry.entity.AhAnimalDeviceLink;
import org.jeecg.modules.animalhusbandry.entity.AhDevice;
import org.jeecg.modules.animalhusbandry.entity.AhTelemetryLatest;
import org.jeecg.modules.animalhusbandry.mapper.AhTelemetryLatestMapper;
import org.jeecg.modules.animalhusbandry.service.impl.TDengineTimeSeriesServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

@Service
@Slf4j
public class TelemetryConsumerService {
    @Autowired
    private TDengineTimeSeriesServiceImpl tdengineService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IAhDeviceService ahDeviceService;

    @Autowired
    private AhTelemetryLatestMapper ahTelemetryLatestMapper;

    @Autowired
    private IAhAnimalDeviceLinkService ahAnimalDeviceLinkService;

    @Autowired
    private IAhAnimalService ahAnimalService;

    private static final String KAFKA_TOPIC = "telemetry_f0c794c0-e903-11ef-a8ee-99a8c68f9649";
    
    @KafkaListener(topics = KAFKA_TOPIC, groupId = "animal-husbandry-group")
    public void consume(String message) {
        log.info("从Kafka主题 '{}' 接收到消息: {}", KAFKA_TOPIC, message);
        try {
            // 1. 将消息解析为通用的JSON树
            JsonNode rootNode = objectMapper.readTree(message);
            JsonNode deviceNameNode = rootNode.path("deviceName");

            if (deviceNameNode.isMissingNode() || !deviceNameNode.isTextual()) {
                log.warn("收到的Kafka消息中缺少有效的deviceName字段，已忽略。消息: {}", message);
                return;
            }
            
            String devEui = deviceNameNode.asText();

            // 2. 写入TDengine (历史数据存储)
            tdengineService.saveRawTelemetryData(devEui, message);

            // 3. 处理遥测数据：存快照并更新主表
            processAndCacheTelemetry(devEui, message, rootNode);


        } catch (JsonProcessingException e) {
            log.error("解析Kafka消息失败: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("处理遥测数据时发生未知错误: {}", e.getMessage(), e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    protected void processAndCacheTelemetry(String devEui, String rawMessage, JsonNode rootNode) {
        // a. 根据DevEUI找到我们的业务设备ID
        AhDevice device = ahDeviceService.getDeviceByDevEui(devEui);
        if (device == null) {
            log.warn("根据DevEUI '{}' 未在ah_device表中找到对应设备，无法处理遥测数据。", devEui);
            return;
        }
        String deviceId = device.getId();

        // b. 保存原始数据到快照表 (执行UPSERT操作)
        AhTelemetryLatest latest = new AhTelemetryLatest();
        latest.setDeviceId(deviceId);
        latest.setTelemetryData(rawMessage);
        latest.setLastUpdateTime(new Date());
        ahTelemetryLatestMapper.delete(new QueryWrapper<AhTelemetryLatest>().lambda().eq(AhTelemetryLatest::getDeviceId, deviceId));
        ahTelemetryLatestMapper.insert(latest);
        log.info("成功更新设备ID '{}' 的MySQL遥测快照。", deviceId);

        // c. 解析关键指标
        BigDecimal temperature = null;
        Integer steps = null;
        String gastricMomentum = null;
        BigDecimal latitude = null;
        BigDecimal longitude = null;

        JsonNode tempNode = findValueCaseInsensitive(rootNode, "temperature");
        if (tempNode != null && tempNode.isNumber()) {
            temperature = BigDecimal.valueOf(tempNode.asDouble());
        }

        JsonNode stepNode = findValueCaseInsensitive(rootNode, "step");
        if (stepNode != null && stepNode.isNumber()) {
            steps = stepNode.asInt();
        }

        JsonNode gastricNode = findValueCaseInsensitive(rootNode, "gastric_momentum");
        if (gastricNode != null) {
            gastricMomentum = gastricNode.asText();
        }

        JsonNode latNode = findValueCaseInsensitive(rootNode, "latitude");
        if (latNode != null && latNode.isNumber()) {
            latitude = BigDecimal.valueOf(latNode.asDouble());
        }
        
        JsonNode lonNode = findValueCaseInsensitive(rootNode, "longitude");
        if (lonNode != null && lonNode.isNumber()) {
            longitude = BigDecimal.valueOf(lonNode.asDouble());
        }
        
        // 如果没有解析到任何一个关键指标，则无需更新主表
        if (temperature == null && steps == null && gastricMomentum == null && latitude == null && longitude == null) {
            log.info("遥测数据中未找到任何关键指标，跳过更新主表。设备ID: {}", deviceId);
            return;
        }

        // d. 查找当前设备绑定的牲畜
        AhAnimalDeviceLink activeLink = ahAnimalDeviceLinkService.getOne(
            new QueryWrapper<AhAnimalDeviceLink>()
                .eq("device_id", deviceId)
                .eq("is_active", 1)
                .last("LIMIT 1")
        );

        if (activeLink == null) {
            log.warn("设备ID '{}' 未找到有效的牲畜绑定记录，无法更新主表。", deviceId);
            return;
        }
        String animalId = activeLink.getAnimalId();

        // e. 更新牲畜主表
        AhAnimal animalToUpdate = new AhAnimal();
        animalToUpdate.setId(animalId);

        if (temperature != null) {
            animalToUpdate.setLatestTemperature(temperature);
        }
        if (steps != null) {
            animalToUpdate.setLatestSteps(steps);
        }
        if (gastricMomentum != null) {
            animalToUpdate.setLatestGastricMomentum(gastricMomentum);
        }
        // 仅当经纬度有效时才更新 (避免被 0,0 覆盖)
        if (latitude != null && longitude != null && latitude.doubleValue() != 0 && longitude.doubleValue() != 0) {
            animalToUpdate.setLastLocationLat(latitude);
            animalToUpdate.setLastLocationLon(longitude);
        }

        boolean success = ahAnimalService.updateById(animalToUpdate);
        if (success) {
            log.info("成功将遥测数据更新到牲畜主表。牲畜ID: {}, 设备ID: {}", animalId, deviceId);
        } else {
            log.error("更新牲畜主表失败。牲畜ID: {}", animalId);
        }
    }
    
    private JsonNode findValueCaseInsensitive(JsonNode jsonNode, String key) {
        if (jsonNode == null || key == null) {
            return null;
        }
        final String lowerCaseKey = key.toLowerCase();
        
        // 1. 在根节点查找
        Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            if (entry.getKey().toLowerCase().equals(lowerCaseKey)) {
                return entry.getValue();
            }
        }
        
        // 2. 在 "data" 子节点查找 (兼容特定遥测格式)
        JsonNode dataNode = jsonNode.get("data");
        if (dataNode != null && dataNode.isObject()) {
             Iterator<Map.Entry<String, JsonNode>> dataFields = dataNode.fields();
             while (dataFields.hasNext()) {
                Map.Entry<String, JsonNode> entry = dataFields.next();
                if (entry.getKey().toLowerCase().equals(lowerCaseKey)) {
                    return entry.getValue();
                }
            }
        }
        
        return null;
    }
}