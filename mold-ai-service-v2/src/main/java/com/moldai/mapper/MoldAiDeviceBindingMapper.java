package com.moldai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moldai.entity.MoldAiDeviceBinding;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MoldAiDeviceBindingMapper extends BaseMapper<MoldAiDeviceBinding> {
    
    @Select("SELECT * FROM mai_device_binding WHERE device_id = #{deviceId} LIMIT 1")
    MoldAiDeviceBinding selectByDeviceId(String deviceId);
}


