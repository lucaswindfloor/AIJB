package org.jeecg.modules.animalhusbandry.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.animalhusbandry.entity.RumenCapsule;
import org.jeecg.modules.animalhusbandry.service.IRumenCapsuleService;
import org.jeecg.modules.animalhusbandry.service.IThingsBoardCapsuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Tag(name="瘤胃胶囊设备管理")
@RestController
@RequestMapping("/animal_husbandry/rumen_capsule")
@Slf4j
public class RumenCapsuleController extends JeecgController<RumenCapsule, IRumenCapsuleService> {

    @Autowired
    private IThingsBoardCapsuleService thingsBoardCapsuleService;

    /**
     * 分页列表查询
     */
    @Operation(summary="瘤胃胶囊设备表-分页列表查询", description="瘤胃胶囊设备表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<RumenCapsule>> queryPageList(RumenCapsule rumenCapsule,
                                                     @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                     @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                     HttpServletRequest req) {
        QueryWrapper<RumenCapsule> queryWrapper = QueryGenerator.initQueryWrapper(rumenCapsule, req.getParameterMap());
        Page<RumenCapsule> page = new Page<>(pageNo, pageSize);
        IPage<RumenCapsule> pageList = service.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 根据ThingsBoard设备ID获取最新遥测数据
     */
    @Operation(summary="获取设备最新遥测数据", description="根据ThingsBoard设备ID获取最新遥测数据")
    @GetMapping("/get-latest-telemetry/{tbDeviceId}")
    public Result<?> getLatestTelemetry(@PathVariable("tbDeviceId") String tbDeviceId) {
        if (tbDeviceId == null || "null".equals(tbDeviceId) || tbDeviceId.trim().isEmpty()) {
            return Result.error("设备ID为空，无法查询遥测数据");
        }
        return thingsBoardCapsuleService.getLatestTelemetry(tbDeviceId);
    }
} 