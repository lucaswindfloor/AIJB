package com.moldai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moldai.entity.MoldAiActuator;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MoldAiActuatorMapper extends BaseMapper<MoldAiActuator> {
    
    /**
     * 根据传感器设备ID查询其关联的所有联动设备
     */
    @Select("SELECT a.* FROM mai_actuator a " +
            "INNER JOIN mai_sensor_actuator_link l ON a.id = l.actuator_id " +
            "WHERE l.sensor_device_id = #{sensorDeviceId} AND a.enabled = 1")
    List<MoldAiActuator> selectBySensorDeviceId(@Param("sensorDeviceId") String sensorDeviceId);
}

