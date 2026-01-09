package org.jeecg.modules.moldai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.moldai.entity.MoldAiRiskResult;

public interface MoldAiRiskResultMapper extends BaseMapper<MoldAiRiskResult> {
    
    @Select("SELECT * FROM mold_ai_risk_result WHERE device_id = #{deviceId} ORDER BY calculated_time DESC LIMIT 1")
    MoldAiRiskResult selectLatestByDeviceId(String deviceId);
}


