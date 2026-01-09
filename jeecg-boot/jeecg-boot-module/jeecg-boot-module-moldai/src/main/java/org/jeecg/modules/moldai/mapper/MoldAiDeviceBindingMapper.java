package org.jeecg.modules.moldai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.moldai.entity.MoldAiDeviceBinding;

import java.util.List;

public interface MoldAiDeviceBindingMapper extends BaseMapper<MoldAiDeviceBinding> {
    
    @Select("SELECT * FROM mold_ai_device_binding WHERE device_id = #{deviceId} LIMIT 1")
    MoldAiDeviceBinding selectByDeviceId(String deviceId);
    
    /**
     * 根据分片获取设备ID列表 (用于XXL-Job分片广播)
     * 假设 device_id 是字符串，我们用 hash code 取模
     */
    @Select("SELECT device_id FROM mold_ai_device_binding WHERE MOD(ABS(CAST(CONV(SUBSTRING(MD5(device_id), 1, 16), 16, 10) AS UNSIGNED)), #{shardTotal}) = #{shardIndex}")
    List<String> selectDeviceIdsByShard(int shardIndex, int shardTotal);
}


