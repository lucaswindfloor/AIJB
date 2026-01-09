package org.jeecg.modules.moldai.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.moldai.entity.MoldAiScene;
import org.jeecg.modules.moldai.service.IMoldAiSceneService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 场景管理接口
 */
@Slf4j
@RestController
@RequestMapping("/mold_ai/scene")
@Tag(name = "霉菌场景管理", description = "霉菌预设场景管理接口")
public class MoldAiSceneController extends JeecgController<MoldAiScene, IMoldAiSceneService> {
    
}


