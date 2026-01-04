package org.jeecg.modules.ai.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.ai.entity.AhAnimal;
import org.jeecg.modules.ai.mapper.AnimalHealthMapper;
import org.jeecg.modules.ai.service.IHealthAnalysisService;
import org.jeecg.modules.ai.service.ITDengineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Description: AI健康分析服务实现类
 * @Author: AI Assistant
 * @Date:   2024-08-24
 * @Version: V1.0
 */
@Service
@Slf4j
public class HealthAnalysisServiceImpl extends ServiceImpl<AnimalHealthMapper, AhAnimal> implements IHealthAnalysisService {

    @Autowired
    private ITDengineService tdengineService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void analyzeAnimalHealth(String animalId, String devEui) {
        log.info("开始分析牲畜健康状况, animalId: {}, devEui: {}", animalId, devEui);

        // 1. 从TDengine拉取过去30天的数据（范围放宽，以应对数据延迟写入）
        List<Map<String, Object>> rawDataList = tdengineService.queryPhysiologicalData(devEui, 720); // 720 hours = 30 days
        if (rawDataList == null || rawDataList.isEmpty()) {
            log.warn("在过去30天内未找到牲畜(设备EUI: {}) 的任何生理数据，分析跳过。", devEui);
            return;
        }

        // 2. 在Java内存中，根据JSON中的真实'ts'进行精确过滤
        long twentyFourHoursAgo = System.currentTimeMillis() / 1000 - 24 * 3600;
        List<JSONObject> filteredData = rawDataList.stream()
            .map(row -> {
                String rawData = (String) row.get("raw_data");
                if (rawData == null || rawData.isEmpty()) return null;
                try {
                    return JSON.parseObject(rawData);
                } catch (Exception e) {
                    log.warn("解析raw_data失败, devEui: {}, data: {}", devEui, rawData, e);
                    return null;
                }
            })
            .filter(json -> {
                if (json == null || !json.containsKey("ts")) return false;
                // 使用JSON中的ts进行时间过滤
                return json.getLongValue("ts") > twentyFourHoursAgo;
            })
            .toList();

        if (filteredData.isEmpty()) {
            log.warn("牲畜(设备EUI: {}) 虽然有历史数据，但在过去24小时内没有新的生理数据，分析跳过。", devEui);
            return;
        }


        // 3. 核心规则分析 (基于过滤后的精确数据)
        String healthStatus = "健康";
        int healthScore = 100;
        StringBuilder conclusion = new StringBuilder("生理指标正常。");

        // 规则1：体温异常检测 (瘤胃胶囊)
        try {
            double maxTemp = filteredData.stream()
                .mapToDouble(json -> {
                    JSONObject dataObj = json.getJSONObject("data");
                    if (dataObj != null && dataObj.containsKey("Temperature")) {
                        return dataObj.getDoubleValue("Temperature");
                    }
                    return 0.0;
                })
                .filter(temp -> temp > 0.0) // 过滤掉无效数据
                .max().orElse(0.0);

            if (maxTemp > 39.5) { // 正常牛体温在38.5-39.5℃
                healthStatus = "异常";
                healthScore -= 40;
                conclusion = new StringBuilder(String.format("检测到最高体温%.1f℃，可能存在发热迹象。", maxTemp));
            }
        } catch (Exception e) {
            log.error("解析体温数据流时发生意外错误, devEui: {}", devEui, e);
        }

        // 规则2：活动量过低检测 (动物追踪器)
        try {
            double totalActivity = filteredData.stream()
                .mapToDouble(json -> {
                    JSONObject dataObj = json.getJSONObject("data");
                    // 动物追踪器使用 'step' 字段作为活动量
                    if (dataObj != null && dataObj.containsKey("step")) {
                        return dataObj.getDoubleValue("step");
                    }
                    return 0.0;
                })
                .sum();

            if (totalActivity < 5000) { // 假设健康牛每日活动量阈值为5000
                if (!"异常".equals(healthStatus)) {
                    healthStatus = "关注";
                }
                healthScore -= 20;
                if(conclusion.toString().equals("生理指标正常。")){
                    conclusion = new StringBuilder("近24小时活动量较低，需关注其精神状态。");
                } else {
                    conclusion.append("且近24小时活动量较低，需关注其精神状态。");
                }
            }
        } catch (Exception e) {
            log.error("解析活动量数据流时发生意外错误, devEui: {}", devEui, e);
        }


        if (healthScore < 0) {
            healthScore = 0;
        }

        // 4. 将分析结果写回MySQL
        AhAnimal updatedAnimal = new AhAnimal();
        updatedAnimal.setId(animalId);
        updatedAnimal.setHealthStatus(healthStatus);
        updatedAnimal.setHealthScore(healthScore);
        updatedAnimal.setAiConclusion(conclusion.toString());

        // 使用MyBatis-Plus提供的 updateById 方法，它会自动处理非空字段的更新
        boolean success = this.updateById(updatedAnimal);
        if(success) {
            log.info("牲畜 {} (设备EUI: {}) 分析完成, 结果: {}", animalId, devEui, conclusion.toString());
        } else {
            log.error("更新牲畜 {} 的健康分析结果失败。", animalId);
        }
    }
}
