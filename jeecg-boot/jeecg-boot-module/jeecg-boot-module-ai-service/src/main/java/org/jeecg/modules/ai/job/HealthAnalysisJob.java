package org.jeecg.modules.ai.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.ai.entity.AhAnimalDeviceLink;
import org.jeecg.modules.ai.mapper.AnimalDeviceLinkMapper;
import org.jeecg.modules.ai.service.IHealthAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @Description: 牲畜健康分析定时任务
 * @Author: AI Assistant
 * @Date:   2024-08-24
 * @Version: V1.0
 */
@Component
@Slf4j
public class HealthAnalysisJob {

    @Autowired
    private IHealthAnalysisService healthAnalysisService;

    @Autowired
    private AnimalDeviceLinkMapper animalDeviceLinkMapper;

    /**
     * AI健康分析任务
     * JobHandler: healthAnalysisJob
     * Cron: 推荐 0 0 * * * ? (每小时执行一次)
     * 任务描述：定时分析所有已绑定设备的牲畜的健康状况。
     */
    @XxlJob("healthAnalysisJob")
    public void execute() {
        log.info("开始执行 [AI健康分析] 定时任务...");

        // 1. 从ah_animal_device_link表查询所有有效的绑定关系
        QueryWrapper<AhAnimalDeviceLink> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_active", 1);
        List<AhAnimalDeviceLink> links = animalDeviceLinkMapper.selectList(queryWrapper);

        if (links == null || links.isEmpty()) {
            log.info("没有找到有效的牲畜设备绑定关系，任务结束。");
            return;
        }

        log.info("查询到 {} 条有效的绑定关系，开始逐一分析...", links.size());

        // 2. 遍历所有绑定关系，调用分析服务
        for (AhAnimalDeviceLink link : links) {
            try {
                // 确保animalId和deviceId都存在
                if (StringUtils.hasText(link.getAnimalId()) && StringUtils.hasText(link.getDeviceId())) {
                    healthAnalysisService.analyzeAnimalHealth(link.getAnimalId(), link.getDeviceId());
                } else {
                    log.warn("发现无效的绑定记录，ID: {}，将跳过分析。", link.getId());
                }
            } catch (Exception e) {
                // 捕获单次分析的异常，防止中断整个任务
                log.error("分析牲畜 [AnimalID: {}] 时发生未捕获的异常", link.getAnimalId(), e);
            }
        }
        
        log.info("[AI健康分析] 定时任务执行完毕，共处理 {} 条绑定关系。", links.size());
    }
}
