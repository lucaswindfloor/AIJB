package org.jeecg.modules.moldai.algorithm;

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
        // 如果数据点不足，需要考虑是否进行计算或标记为置信度低，这里简化处理，直接计算
        double gSum = 0;
        for (DataPoint dp : dataPoints) {
            if (dp.getTemperature() == null || dp.getHumidity() == null) {
                continue;
            }
            double g = calculateG(dp.getTemperature(), dp.getHumidity(), scene);
            // G值通常是累积的，如果环境改善（如湿度降低），霉菌可能会停止生长甚至消退（模型中MI可减小），
            // 但G值本身通常表示生长的潜力。VTT模型中，当RH < RH_crit时，会有衰减。
            // 这里我们采用简化的累积逻辑：正值累积，负值（不适宜环境）扣减。
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
     * 实际项目中，这里应该替换为查表逻辑 (vtt-lookup-table.csv)
     */
    private double calculateG(double temp, double humidity, SceneParam scene) {
        // 霉菌生长基本条件：温度5-40℃，湿度>65%（对于某些敏感材料可能更低）
        // 这里的65%是一个通用基准，实际应根据材料等级动态调整
        
        // 1. 临界湿度 RH_crit 计算 (VTT模型简化)
        // 不同的材料等级(materialLevel) 对应不同的临界湿度
        // 0级(非常敏感) ~ 80%, 3级(中等) ~ 85%, ... (这里仅作示意)
        // 我们用一个简化的线性关系模拟材料影响：材料等级越高，越耐受霉菌，临界湿度越高
        // 假设 Material Level 1.0 (敏感) -> RH_crit = 75%
        // Material Level 6.0 (耐受) -> RH_crit = 95%
        double materialBaseRh = 70.0 + (scene.getMaterialLevel().doubleValue() * 4.0);
        
        if (humidity < materialBaseRh || temp < 5 || temp > 40) {
            // 环境不适宜，霉菌指数衰减
            // 衰减系数也可以根据环境干燥程度调整，这里给一个固定负值模拟衰减
            return -0.05; 
        }
        
        // 2. 生长潜力计算
        // 湿度因子：湿度越高生长越快
        double humidityFactor = (humidity - materialBaseRh) / (100.0 - materialBaseRh);
        
        // 温度因子：25-30度最快，两端慢
        // 使用一个抛物线模拟温度影响: 1 - ((T - 27)/15)^2
        double tempDist = Math.abs(temp - 27.0);
        double tempFactor = Math.max(0, 1.0 - Math.pow(tempDist / 15.0, 2));
        
        // 综合G值
        return humidityFactor * tempFactor * 0.5; // 0.5是时间尺度系数，调整生长速度量级
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


