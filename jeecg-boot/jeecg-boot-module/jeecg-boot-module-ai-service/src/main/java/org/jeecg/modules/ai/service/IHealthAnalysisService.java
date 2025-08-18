package org.jeecg.modules.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.ai.entity.AhAnimal;

/**
 * @Description: AI健康分析服务接口
 * @Author: AI Assistant
 * @Date:   2024-08-24
 * @Version: V1.0
 */
public interface IHealthAnalysisService extends IService<AhAnimal> {

    /**
     * 对单个牲畜进行健康分析
     * @param animalId 牲畜ID
     * @param deviceId 绑定的设备ID
     */
    void analyzeAnimalHealth(String animalId, String deviceId);

}
