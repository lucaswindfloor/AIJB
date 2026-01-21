package com.moldai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moldai.entity.MoldAiRiskResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MoldAiRiskResultMapper extends BaseMapper<MoldAiRiskResult> {

    @Select("SELECT * FROM mai_risk_result WHERE device_id = #{deviceId} ORDER BY calculated_time DESC LIMIT 1")
    MoldAiRiskResult selectLatestByDeviceId(String deviceId);

    @Select("SELECT * FROM mai_risk_result WHERE device_id = #{deviceId} ORDER BY calculated_time DESC LIMIT #{limit}")
    List<MoldAiRiskResult> selectHistory(String deviceId, int limit);
}
