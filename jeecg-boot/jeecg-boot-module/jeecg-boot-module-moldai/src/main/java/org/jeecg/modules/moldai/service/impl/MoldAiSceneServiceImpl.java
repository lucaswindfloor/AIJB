package org.jeecg.modules.moldai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.moldai.entity.MoldAiScene;
import org.jeecg.modules.moldai.mapper.MoldAiSceneMapper;
import org.jeecg.modules.moldai.service.IMoldAiSceneService;
import org.springframework.stereotype.Service;

@Service
public class MoldAiSceneServiceImpl extends ServiceImpl<MoldAiSceneMapper, MoldAiScene> implements IMoldAiSceneService {
    
    @Override
    public MoldAiScene getByCode(String code) {
        return baseMapper.selectByCode(code);
    }
}


