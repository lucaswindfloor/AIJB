package org.jeecg.modules.moldai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.moldai.entity.MoldAiScene;

public interface IMoldAiSceneService extends IService<MoldAiScene> {
    MoldAiScene getByCode(String code);
}


