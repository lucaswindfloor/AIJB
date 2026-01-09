package com.moldai.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.moldai.entity.MoldAiDeviceBinding;
import com.moldai.mapper.MoldAiDeviceBindingMapper;
import com.moldai.service.IMoldAiAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class AnalysisJob {

    @Autowired
    private MoldAiDeviceBindingMapper bindingMapper;

    @Autowired
    private IMoldAiAnalysisService analysisService;

    /**
     * 定时分析任务
     * 默认每小时执行一次，cron 表达式可配置
     */
    @Scheduled(cron = "${mold-ai.scheduler.cron:0 0 * * * ?}")
    public void runAnalysis() {
        log.info("=== Scheduled Analysis Job Started ===");
        
        // 1. 获取所有已绑定的设备
        // 注意：如果设备量大，这里应该分页处理，但 MVP 阶段先查全部
        List<MoldAiDeviceBinding> bindings = bindingMapper.selectList(new LambdaQueryWrapper<>());
        
        if (bindings.isEmpty()) {
            log.info("No bound devices found. Job finished.");
            return;
        }

        log.info("Found {} devices to analyze.", bindings.size());

        // 2. 逐个分析
        int successCount = 0;
        int failCount = 0;

        for (MoldAiDeviceBinding binding : bindings) {
            try {
                analysisService.analyze(binding.getDeviceId());
                successCount++;
            } catch (Exception e) {
                log.error("Failed to analyze device [{}]: {}", binding.getDeviceId(), e.getMessage());
                failCount++;
            }
        }

        log.info("=== Scheduled Job Finished. Success: {}, Fail: {} ===", successCount, failCount);
    }
}

