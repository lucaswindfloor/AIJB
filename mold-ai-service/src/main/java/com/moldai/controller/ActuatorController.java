package com.moldai.controller;

import com.moldai.entity.MoldAiActuator;
import com.moldai.service.IActuatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 联动设备管理接口
 */
@RestController
@RequestMapping("/api/v1/mold-ai/actuator")
@CrossOrigin(origins = "*")
public class ActuatorController {
    
    @Autowired
    private IActuatorService actuatorService;
    
    /**
     * 获取所有联动设备列表
     */
    @GetMapping("/list")
    public ApiResponse<List<MoldAiActuator>> list() {
        return ApiResponse.success(actuatorService.listAll());
    }
    
    /**
     * 根据ID获取联动设备
     */
    @GetMapping("/{id}")
    public ApiResponse<MoldAiActuator> getById(@PathVariable String id) {
        return ApiResponse.success(actuatorService.getById(id));
    }
    
    /**
     * 添加联动设备
     */
    @PostMapping("/add")
    public ApiResponse<MoldAiActuator> add(@RequestBody MoldAiActuator actuator) {
        try {
            MoldAiActuator result = actuatorService.add(actuator);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.fail("添加失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新联动设备
     */
    @PutMapping("/update")
    public ApiResponse<Boolean> update(@RequestBody MoldAiActuator actuator) {
        try {
            boolean result = actuatorService.update(actuator);
            return result ? ApiResponse.success(true) : ApiResponse.fail("更新失败");
        } catch (Exception e) {
            return ApiResponse.fail("更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除联动设备
     */
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Boolean> delete(@PathVariable String id) {
        try {
            boolean result = actuatorService.delete(id);
            return result ? ApiResponse.success(true) : ApiResponse.fail("删除失败");
        } catch (Exception e) {
            return ApiResponse.fail("删除失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取传感器关联的联动设备
     */
    @GetMapping("/sensor/{sensorDeviceId}")
    public ApiResponse<List<MoldAiActuator>> getActuatorsBySensor(@PathVariable String sensorDeviceId) {
        return ApiResponse.success(actuatorService.getActuatorsBySensor(sensorDeviceId));
    }
    
    /**
     * 为传感器绑定联动设备
     */
    @PostMapping("/sensor/{sensorDeviceId}/bind")
    public ApiResponse<Boolean> bindActuators(
            @PathVariable String sensorDeviceId,
            @RequestBody BindActuatorsRequest request) {
        try {
            actuatorService.bindActuatorsToSensor(
                sensorDeviceId, 
                request.getActuatorIds(), 
                request.getTriggerLevel()
            );
            return ApiResponse.success(true);
        } catch (Exception e) {
            return ApiResponse.fail("绑定失败: " + e.getMessage());
        }
    }
    
    /**
     * 手动触发传感器关联的联动设备
     */
    @PostMapping("/sensor/{sensorDeviceId}/trigger")
    public ApiResponse<Boolean> triggerActuators(
            @PathVariable String sensorDeviceId,
            @RequestParam(defaultValue = "01") String command) {
        try {
            actuatorService.triggerActuators(sensorDeviceId, command);
            return ApiResponse.success(true);
        } catch (Exception e) {
            return ApiResponse.fail("触发失败: " + e.getMessage());
        }
    }
    
    /**
     * 绑定联动设备请求体
     */
    @lombok.Data
    public static class BindActuatorsRequest {
        private List<String> actuatorIds;
        private String triggerLevel;
    }
}


