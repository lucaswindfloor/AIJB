package com.moldai.controller;

import com.moldai.entity.MoldAiDeviceBinding;
import com.moldai.mapper.MoldAiDeviceBindingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/v1/mold-ai/binding")
public class MoldAiDeviceBindingController {

    @Autowired
    private MoldAiDeviceBindingMapper bindingMapper;

    @PostMapping("/bind")
    public ApiResponse<String> bindDevice(@RequestParam String deviceId, @RequestParam String sceneId) {
        // 先查询是否已存在
        MoldAiDeviceBinding existing = bindingMapper.selectByDeviceId(deviceId);
        if (existing != null) {
            // 更新
            existing.setSceneId(sceneId);
            bindingMapper.updateById(existing);
            return ApiResponse.success("Device binding updated");
        } else {
            // 新增
            MoldAiDeviceBinding binding = new MoldAiDeviceBinding();
            binding.setDeviceId(deviceId);
            binding.setSceneId(sceneId);
            binding.setCreateTime(new Date());
            bindingMapper.insert(binding);
            return ApiResponse.success("Device bound successfully");
        }
    }
}

