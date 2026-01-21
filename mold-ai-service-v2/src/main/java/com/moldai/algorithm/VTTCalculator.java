package com.moldai.algorithm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * VTT霉菌生长模型计算器（工程简化版）
 * 
 * 核心功能：温湿度数据 → MI值 → 风险等级
 */
@Slf4j
@Component
public class VTTCalculator {
    
    /**
     * 计算风险结果
     */
    public RiskResult calculate(String deviceId, 
                                List<DataPoint> dataPoints,
                                SceneParam scene) {
        
        // 1. 计算7天G值总和
        double gSum = 0;
        for (DataPoint dp : dataPoints) {
            if (dp.getTemperature() == null || dp.getHumidity() == null) {
                continue;
            }
            double g = calculateG(dp.getTemperature(), dp.getHumidity(), scene);
            gSum += g;
        }
        
        // 确保MI不为负
        BigDecimal mi = BigDecimal.valueOf(Math.max(0, gSum / 168.0))  // 简单归一化到7天（假设每小时一个点）
                .setScale(4, RoundingMode.HALF_UP);
        
        // 3. 评估风险等级
        String level;
        if (mi.compareTo(scene.getThresholdHigh()) >= 0) {
            level = "HIGH";
        } else if (mi.compareTo(scene.getThresholdLow()) >= 0) {
            level = "MEDIUM";
        } else {
            level = "LOW";
        }
        
        return new RiskResult(deviceId, mi, level);
    }
    
    /**
     * 计算单点G值（简化版：基于温湿度直接计算）
     */
    private double calculateG(double temp, double humidity, SceneParam scene) {
        // 1. 临界湿度 RH_crit 计算
        double materialBaseRh = 70.0 + (scene.getMaterialLevel().doubleValue() * 4.0);
        
        if (humidity < materialBaseRh || temp < 5 || temp > 40) {
            return -0.05; 
        }
        
        // 2. 生长潜力计算
        // 湿度因子
        double humidityFactor = (humidity - materialBaseRh) / (100.0 - materialBaseRh);
        
        // 温度因子
        double tempDist = Math.abs(temp - 27.0);
        double tempFactor = Math.max(0, 1.0 - Math.pow(tempDist / 15.0, 2));
        
        // 综合G值
        return humidityFactor * tempFactor * 0.5; 
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DataPoint {
        private Long timestamp;
        private Double temperature;
        private Double humidity;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SceneParam {
        private BigDecimal materialLevel;
        private BigDecimal thresholdLow;
        private BigDecimal thresholdHigh;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RiskResult {
        private String deviceId;
        private BigDecimal miValue;
        private String riskLevel;
    }
}


