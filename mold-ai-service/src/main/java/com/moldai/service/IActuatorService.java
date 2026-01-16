package com.moldai.service;

import com.moldai.entity.MoldAiActuator;

import java.util.List;

/**
 * 联动设备管理服务接口
 */
public interface IActuatorService {
    
    /**
     * 获取所有联动设备列表
     */
    List<MoldAiActuator> listAll();
    
    /**
     * 根据ID获取联动设备
     */
    MoldAiActuator getById(String id);
    
    /**
     * 添加联动设备
     */
    MoldAiActuator add(MoldAiActuator actuator);
    
    /**
     * 更新联动设备
     */
    boolean update(MoldAiActuator actuator);
    
    /**
     * 删除联动设备
     */
    boolean delete(String id);
    
    /**
     * 根据传感器设备ID获取其关联的联动设备
     */
    List<MoldAiActuator> getActuatorsBySensor(String sensorDeviceId);
    
    /**
     * 为传感器绑定联动设备
     * @param sensorDeviceId 传感器设备ID
     * @param actuatorIds 联动设备ID列表
     * @param triggerLevel 触发风险等级
     */
    void bindActuatorsToSensor(String sensorDeviceId, List<String> actuatorIds, String triggerLevel);
    
    /**
     * 触发传感器关联的所有联动设备
     * @param sensorDeviceId 传感器设备ID
     * @param command 控制指令
     */
    void triggerActuators(String sensorDeviceId, String command);
}

