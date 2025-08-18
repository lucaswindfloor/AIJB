package org.jeecg.modules.ai.service.impl;

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
    public void analyzeAnimalHealth(String animalId, String deviceId) {
        log.info("开始分析牲畜健康状况, animalId: {}, deviceId: {}", animalId, deviceId);

        // 1. 从TDengine拉取过去24小时数据
        List<Map<String, Object>> data = tdengineService.queryPhysiologicalData(deviceId, 24);
        if (data == null || data.isEmpty()) {
            log.warn("未找到牲畜 {} 的生理数据，分析跳过。", deviceId);
            return;
        }

        // 2. 核心规则分析 (MVP版本)
        String healthStatus = "健康";
        int healthScore = 100;
        StringBuilder conclusion = new StringBuilder("生理指标正常。");

        // 规则1：体温异常检测
        // 使用 try-catch 防止数据类型转换异常
        try {
            double maxTemp = data.stream()
                .mapToDouble(row -> {
                    Object temp = row.get("temperature");
                    return temp instanceof Number ? ((Number) temp).doubleValue() : 0.0;
                })
                .max().orElse(0);

            if (maxTemp > 39.5) { // 正常牛体温在38.5-39.5℃
                healthStatus = "异常";
                healthScore -= 40;
                conclusion = new StringBuilder(String.format("检测到最高体温%.1f℃，可能存在发热迹象。", maxTemp));
            }
        } catch (Exception e) {
            log.error("解析体温数据时出错, deviceId: {}", deviceId, e);
        }

        // 规则2：活动量过低检测
        try {
            double totalActivity = data.stream()
                .mapToDouble(row -> {
                    Object activity = row.get("activity");
                    return activity instanceof Number ? ((Number) activity).doubleValue() : 0.0;
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
            log.error("解析活动量数据时出错, deviceId: {}", deviceId, e);
        }


        if (healthScore < 0) {
            healthScore = 0;
        }

        // 3. 将分析结果写回MySQL
        AhAnimal updatedAnimal = new AhAnimal();
        updatedAnimal.setId(animalId);
        updatedAnimal.setHealthStatus(healthStatus);
        updatedAnimal.setHealthScore(healthScore);
        updatedAnimal.setAiConclusion(conclusion.toString());

        // 使用MyBatis-Plus提供的 updateById 方法，它会自动处理非空字段的更新
        boolean success = this.updateById(updatedAnimal);
        if(success) {
            log.info("牲畜 {} 分析完成, 结果: {}", animalId, conclusion.toString());
        } else {
            log.error("更新牲畜 {} 的健康分析结果失败。", animalId);
        }
    }
}
