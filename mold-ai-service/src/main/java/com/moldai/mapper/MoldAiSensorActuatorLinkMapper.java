package com.moldai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moldai.entity.MoldAiSensorActuatorLink;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MoldAiSensorActuatorLinkMapper extends BaseMapper<MoldAiSensorActuatorLink> {
    
    /**
     * 删除传感器的所有联动设备关联
     */
    @Delete("DELETE FROM mai_sensor_actuator_link WHERE sensor_device_id = #{sensorDeviceId}")
    int deleteBySensorDeviceId(@Param("sensorDeviceId") String sensorDeviceId);
    
    /**
     * 删除某个联动设备的所有关联（当删除联动设备时调用）
     */
    @Delete("DELETE FROM mai_sensor_actuator_link WHERE actuator_id = #{actuatorId}")
    int deleteByActuatorId(@Param("actuatorId") String actuatorId);
}


