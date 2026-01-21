package com.moldai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.moldai.entity.MoldAiActuator;
import com.moldai.entity.MoldAiSensorActuatorLink;
import com.moldai.mapper.MoldAiActuatorMapper;
import com.moldai.mapper.MoldAiSensorActuatorLinkMapper;
import com.moldai.service.IActuatorService;
import com.moldai.service.IDeviceControlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ActuatorServiceImpl implements IActuatorService {
    
    @Autowired
    private MoldAiActuatorMapper actuatorMapper;
    
    @Autowired
    private MoldAiSensorActuatorLinkMapper linkMapper;
    
    @Autowired
    private IDeviceControlService deviceControlService;
    
    @Override
    public List<MoldAiActuator> listAll() {
        return actuatorMapper.selectList(
            new LambdaQueryWrapper<MoldAiActuator>()
                .eq(MoldAiActuator::getEnabled, 1)
                .orderByDesc(MoldAiActuator::getCreateTime)
        );
    }
    
    @Override
    public MoldAiActuator getById(String id) {
        return actuatorMapper.selectById(id);
    }
    
    @Override
    public MoldAiActuator add(MoldAiActuator actuator) {
        actuator.setEnabled(1);
        actuatorMapper.insert(actuator);
        return actuator;
    }
    
    @Override
    public boolean update(MoldAiActuator actuator) {
        return actuatorMapper.updateById(actuator) > 0;
    }
    
    @Override
    @Transactional
    public boolean delete(String id) {
        // 先删除关联关系
        linkMapper.deleteByActuatorId(id);
        // 再删除设备
        return actuatorMapper.deleteById(id) > 0;
    }
    
    @Override
    public List<MoldAiActuator> getActuatorsBySensor(String sensorDeviceId) {
        return actuatorMapper.selectBySensorDeviceId(sensorDeviceId);
    }
    
    @Override
    @Transactional
    public void bindActuatorsToSensor(String sensorDeviceId, List<String> actuatorIds, String triggerLevel) {
        // 先删除旧的关联
        linkMapper.deleteBySensorDeviceId(sensorDeviceId);
        
        // 创建新的关联
        if (actuatorIds != null && !actuatorIds.isEmpty()) {
            for (String actuatorId : actuatorIds) {
                MoldAiSensorActuatorLink link = new MoldAiSensorActuatorLink();
                link.setSensorDeviceId(sensorDeviceId);
                link.setActuatorId(actuatorId);
                link.setTriggerLevel(triggerLevel != null ? triggerLevel : "HIGH");
                linkMapper.insert(link);
            }
        }
        
        log.info("Sensor [{}] bindActuators: {}", sensorDeviceId, actuatorIds);
    }
    
    @Override
    public void triggerActuators(String sensorDeviceId, String command) {
        List<MoldAiActuator> actuators = getActuatorsBySensor(sensorDeviceId);
        
        if (actuators == null || actuators.isEmpty()) {
            log.info("Sensor [{}] has no linked actuators", sensorDeviceId);
            return;
        }
        
        for (MoldAiActuator actuator : actuators) {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("lockControl", command);
                
                String rpcMethod = actuator.getRpcMethod();
                if (rpcMethod == null || rpcMethod.isEmpty()) {
                    rpcMethod = "LockControl";
                }
                
                boolean success = deviceControlService.sendRpcCommand(
                    actuator.getDeviceId(), 
                    rpcMethod, 
                    params
                );
                
                if (success) {
                    log.info("Triggered actuator [{}] {} with command [{}]", 
                        actuator.getDeviceName(), actuator.getDeviceId(), command);
                } else {
                    log.error("Failed to trigger actuator [{}]", actuator.getDeviceName());
                }
            } catch (Exception e) {
                log.error("Error triggering actuator [{}]: {}", actuator.getDeviceName(), e.getMessage());
            }
        }
    }
}


