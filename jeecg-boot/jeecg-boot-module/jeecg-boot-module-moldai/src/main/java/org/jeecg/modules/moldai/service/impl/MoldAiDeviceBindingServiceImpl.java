package org.jeecg.modules.moldai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.moldai.entity.MoldAiDeviceBinding;
import org.jeecg.modules.moldai.mapper.MoldAiDeviceBindingMapper;
import org.jeecg.modules.moldai.service.IMoldAiDeviceBindingService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MoldAiDeviceBindingServiceImpl extends ServiceImpl<MoldAiDeviceBindingMapper, MoldAiDeviceBinding> implements IMoldAiDeviceBindingService {
    
    @Override
    public List<String> getDeviceIdsByShard(int shardIndex, int shardTotal) {
        return baseMapper.selectDeviceIdsByShard(shardIndex, shardTotal);
    }
}


