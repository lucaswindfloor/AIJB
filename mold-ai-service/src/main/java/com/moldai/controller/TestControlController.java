package com.moldai.controller;

import com.moldai.service.IDeviceControlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/test/control")
public class TestControlController {

    @Autowired
    private IDeviceControlService deviceControlService;

    @RequestMapping(value = "/lock", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResponse<String> controlLock(@RequestParam String deviceId, 
                                           @RequestParam String command) {
        // command: "01" (升起/锁定) or "02" (降下/解锁)
        
        log.info("Testing Lock Control: Device={}, Command={}", deviceId, command);
        
        Map<String, String> params = new HashMap<>();
        params.put("lockControl", command);
        
        // 注意：用户提供的 debug 信息显示是 twoway 调用
        boolean success = deviceControlService.sendRpcCommand(deviceId, "LockControl", params);
        
        if (success) {
            return ApiResponse.success("Command sent successfully");
        } else {
            return ApiResponse.fail("Failed to send command");
        }
    }
}



