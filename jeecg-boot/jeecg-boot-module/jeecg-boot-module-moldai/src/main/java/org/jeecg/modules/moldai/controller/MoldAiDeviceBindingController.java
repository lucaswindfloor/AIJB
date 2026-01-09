package org.jeecg.modules.moldai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.moldai.entity.MoldAiDeviceBinding;
import org.jeecg.modules.moldai.entity.MoldAiScene;
import org.jeecg.modules.moldai.service.IMoldAiDeviceBindingService;
import org.jeecg.modules.moldai.service.IMoldAiSceneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 设备场景绑定接口
 */
@Slf4j
@RestController
@RequestMapping("/mold_ai/binding")
@Tag(name = "设备场景绑定", description = "管理设备与AI场景的关联关系")
public class MoldAiDeviceBindingController extends JeecgController<MoldAiDeviceBinding, IMoldAiDeviceBindingService> {

    @Autowired
    private IMoldAiSceneService sceneService;

    /**
     * 绑定设备到场景
     * 如果设备已绑定，则更新场景
     */
    @PostMapping("/bind")
    @Operation(summary = "绑定设备场景", description = "将设备绑定到指定场景，如果已绑定则更新")
    public Result<?> bindDevice(@RequestBody MoldAiDeviceBinding binding) {
        String deviceId = binding.getDeviceId();
        String sceneId = binding.getSceneId();

        if (deviceId == null || sceneId == null) {
            return Result.error("设备ID和场景ID不能为空");
        }

        // 校验场景是否存在
        MoldAiScene scene = sceneService.getById(sceneId);
        if (scene == null) {
            return Result.error("指定的场景ID不存在");
        }

        // 查询是否已绑定
        LambdaQueryWrapper<MoldAiDeviceBinding> query = new LambdaQueryWrapper<>();
        query.eq(MoldAiDeviceBinding::getDeviceId, deviceId);
        MoldAiDeviceBinding exist = service.getOne(query);

        if (exist != null) {
            // 更新
            exist.setSceneId(sceneId);
            service.updateById(exist);
            return Result.OK("更新绑定成功");
        } else {
            // 新增
            service.save(binding);
            return Result.OK("绑定成功");
        }
    }

    /**
     * 查询设备当前的绑定信息
     */
    @GetMapping("/query")
    @Operation(summary = "查询设备绑定", description = "查询指定设备的当前场景绑定信息")
    public Result<MoldAiDeviceBinding> queryBinding(@RequestParam String deviceId) {
        LambdaQueryWrapper<MoldAiDeviceBinding> query = new LambdaQueryWrapper<>();
        query.eq(MoldAiDeviceBinding::getDeviceId, deviceId);
        MoldAiDeviceBinding binding = service.getOne(query);
        return Result.OK(binding);
    }
    
    /**
     * 解除绑定
     */
    @DeleteMapping("/unbind")
    @Operation(summary = "解除绑定", description = "删除设备的场景绑定关系")
    public Result<?> unbindDevice(@RequestParam String deviceId) {
        LambdaQueryWrapper<MoldAiDeviceBinding> query = new LambdaQueryWrapper<>();
        query.eq(MoldAiDeviceBinding::getDeviceId, deviceId);
        service.remove(query);
        return Result.OK("解绑成功");
    }
}


