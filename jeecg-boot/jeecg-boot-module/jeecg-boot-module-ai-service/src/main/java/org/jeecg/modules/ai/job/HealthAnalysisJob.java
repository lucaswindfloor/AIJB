package org.jeecg.modules.ai.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.ai.entity.AhAnimalDeviceLink;
import org.jeecg.modules.ai.entity.AiDevice;
import org.jeecg.modules.ai.mapper.AiDeviceMapper;
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

    @Autowired
    private AiDeviceMapper aiDeviceMapper;

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
                    // 关键修正：使用 link.getDeviceId() (设备主键) 查询 ah_device 表，获取 dev_eui
                    AiDevice device = aiDeviceMapper.selectById(link.getDeviceId());
                    if (device != null && StringUtils.hasText(device.getDevEui())) {
                        String devEui = device.getDevEui();
                        // 将正确的 dev_eui 传递给分析服务
                        healthAnalysisService.analyzeAnimalHealth(link.getAnimalId(), devEui);
                    } else {
                        log.warn("根据设备ID: {} 未在ah_device表中找到对应的设备或dev_eui，跳过分析。", link.getDeviceId());
                    }
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
