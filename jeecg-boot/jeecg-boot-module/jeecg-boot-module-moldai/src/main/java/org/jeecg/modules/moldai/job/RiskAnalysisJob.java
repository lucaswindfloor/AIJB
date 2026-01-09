package org.jeecg.modules.moldai.job;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.moldai.service.IMoldAiAnalysisService;
import org.jeecg.modules.moldai.service.IMoldAiDeviceBindingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RiskAnalysisJob {

    @Autowired
    private IMoldAiAnalysisService analysisService;
    
    @Autowired
    private IMoldAiDeviceBindingService bindingService;

    /**
     * 霉菌风险分析任务
     * 混合兼容模式：
     * 1. 方法签名：使用旧版 ReturnT<String> execute(String param) 以通过 JeecgBoot 的反射检查
     * 2. 内部逻辑：使用新版 XxlJobHelper (因为编译依赖是 2.4.1)
     */
    @XxlJob("moldRiskAnalysisJob")
    public ReturnT<String> execute(String param) {
        // 1. 获取分片参数 (使用2.4.1 API)
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        
        log.info("开始执行风险分析任务, 分片信息: {}/{}", shardIndex, shardTotal);
        XxlJobHelper.log("开始执行风险分析任务, 分片信息: " + shardIndex + "/" + shardTotal);

        // 2. 获取该分片负责的设备列表
        // 如果 shardTotal <= 1，则获取所有设备
        List<String> deviceIds = bindingService.getDeviceIdsByShard(shardIndex, Math.max(1, shardTotal));

        // 3. 执行分析
        for (String deviceId : deviceIds) {
            try {
                analysisService.analyze(deviceId);
            } catch (Exception e) {
                log.error("设备 {} 分析失败", deviceId, e);
                XxlJobHelper.log("设备 " + deviceId + " 分析失败: " + e.getMessage());
                // 即使单个失败，也不要阻断整个循环
            }
        }
        
        String msg = "任务完成, 处理设备数: " + deviceIds.size();
        log.info(msg);
        XxlJobHelper.log(msg);
        
        return ReturnT.SUCCESS;
    }
}
