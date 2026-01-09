package org.jeecg.modules.moldai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.moldai.entity.MoldAiDeviceBinding;

import java.util.List;

public interface IMoldAiDeviceBindingService extends IService<MoldAiDeviceBinding> {
    List<String> getDeviceIdsByShard(int shardIndex, int shardTotal);
}


