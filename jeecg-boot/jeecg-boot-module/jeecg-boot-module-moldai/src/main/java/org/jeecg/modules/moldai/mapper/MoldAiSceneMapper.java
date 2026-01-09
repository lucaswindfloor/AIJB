package org.jeecg.modules.moldai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.moldai.entity.MoldAiScene;

public interface MoldAiSceneMapper extends BaseMapper<MoldAiScene> {
    
    @Select("SELECT * FROM mold_ai_scene WHERE scene_code = #{code} LIMIT 1")
    MoldAiScene selectByCode(String code);
}


