package org.jeecg.modules.animalhusbandry.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.animalhusbandry.entity.AhAnimal;
import org.jeecg.modules.animalhusbandry.service.IAhAnimalService;
import org.jeecg.modules.animalhusbandry.service.IAhDeviceService;
import org.jeecg.modules.animalhusbandry.service.IAhThingsBoardDeviceService;
import org.jeecg.modules.animalhusbandry.vo.AhAnimalDetailVo;
import org.jeecg.modules.animalhusbandry.vo.AhDeviceBindDTO;
import org.jeecg.modules.animalhusbandry.vo.AhDeviceUnbindDTO;
import org.jeecg.modules.animalhusbandry.vo.BindDeviceDto;
import org.jeecg.modules.animalhusbandry.vo.LifecycleEventDto;
import org.jeecg.modules.animalhusbandry.vo.UnbindDeviceDto;
import org.jeecg.modules.animalhusbandry.service.impl.TDengineTimeSeriesServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeecg.modules.animalhusbandry.vo.AhAnimalPageVO;

@Tag(name="牲畜档案管理")
@RestController
@RequestMapping("/animal_husbandry/animal")
@Slf4j
public class AhAnimalController extends JeecgController<AhAnimal, IAhAnimalService> {
    @Autowired
    private IAhAnimalService ahAnimalService;

    @Autowired
    private IAhDeviceService ahDeviceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    @Qualifier("ahThingsBoardDeviceServiceImpl")
    private IAhThingsBoardDeviceService ahThingsBoardDeviceService;

    @Operation(summary = "牲畜档案-分页列表查询", description = "牲畜档案-分页列表查询")
    @GetMapping(value = "/list")
    // @RequiresPermissions("animal_husbandry:animal:list")
    @AutoLog(value = "牲畜档案-分页列表查询")
    public Result<IPage<AhAnimalPageVO>> queryPageList(AhAnimal ahAnimal,
                                                     @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                     @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                     HttpServletRequest req) {
        QueryWrapper<AhAnimal> queryWrapper = QueryGenerator.initQueryWrapper(ahAnimal, req.getParameterMap());
        Page<AhAnimalPageVO> page = new Page<>(pageNo, pageSize);
        IPage<AhAnimalPageVO> pageList = ahAnimalService.queryAnimalPage(page, queryWrapper);
        return Result.OK(pageList);
    }

    @Operation(summary = "牲畜档案-添加", description = "牲畜档案-添加")
    @PostMapping(value = "/add")
    // @RequiresPermissions("animal_husbandry:animal:add")
    @AutoLog(value = "牲畜档案-添加")
    public Result<String> add(@RequestBody AhAnimal ahAnimal) {
        ahAnimalService.save(ahAnimal);
        return Result.OK("添加成功！");
    }

    @Operation(summary = "牲畜档案-编辑", description = "牲畜档案-编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    // @RequiresPermissions("animal_husbandry:animal:edit")
    @AutoLog(value = "牲畜档案-编辑")
    public Result<String> edit(@RequestBody AhAnimal ahAnimal) {
        ahAnimalService.updateById(ahAnimal);
        return Result.OK("编辑成功!");
    }

    @Operation(summary = "牲畜档案-通过id删除", description = "牲畜档案-通过id删除")
    @DeleteMapping(value = "/delete")
    // @RequiresPermissions("animal_husbandry:animal:delete")
    @AutoLog(value = "牲畜档案-通过id删除")
    public Result<String> delete(@RequestParam(name="id",required=true) String id) {
        ahAnimalService.removeById(id);
        return Result.OK("删除成功!");
    }

    @Operation(summary = "牲畜档案-批量删除", description = "牲畜档案-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
        this.ahAnimalService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    @Operation(summary = "获取牲畜详情", description = "通过id获取牲畜详情，包括关联信息")
    @GetMapping(value = "/queryById")
    public Result<AhAnimalDetailVo> queryById(@RequestParam(name="id",required=true) String id) {
        AhAnimalDetailVo ahAnimalDetailVo = ahAnimalService.getAnimalDetailById(id);
        if(ahAnimalDetailVo==null) {
            return Result.error("未找到对应数据");
        }
        try {
            log.info("查询牲畜详情返回数据: {}", objectMapper.writeValueAsString(ahAnimalDetailVo));
        } catch (JsonProcessingException e) {
            log.error("序列化牲畜详情VO到JSON失败", e);
        }
        return Result.OK(ahAnimalDetailVo);
    }

    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, AhAnimal ahAnimal) {
        return super.exportXls(request, ahAnimal, AhAnimal.class, "牲畜档案表");
    }

    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, AhAnimal.class);
    }

    @Operation(summary = "【重构】将设备绑定到牲畜", description = "调用统一的设备服务逻辑进行绑定")
    @PostMapping(value = "/bindDevice")
    public Result<String> bindDevice(@RequestBody AhDeviceBindDTO bindDeviceDto) {
        try {
            ahDeviceService.bindDeviceToAnimal(bindDeviceDto);
            return Result.OK("绑定成功！");
        } catch (Exception e) {
            log.error("绑定设备失败", e);
            return Result.error("绑定失败：" + e.getMessage());
        }
    }

    @Operation(summary = "【重构】将设备从牲畜解绑", description = "调用统一的设备服务逻辑进行解绑")
    @PostMapping(value = "/unbindDevice")
    public Result<String> unbindDevice(@RequestBody AhDeviceUnbindDTO unbindDeviceDto) {
        try {
            ahDeviceService.unbindFromAnimal(unbindDeviceDto);
            return Result.OK("解绑成功！");
        } catch (Exception e) {
            log.error("解绑设备失败", e);
            return Result.error("解绑失败：" + e.getMessage());
        }
    }

    @Operation(summary = "为牲畜添加生命周期事件", description = "为牲畜添加生命周期事件")
    @PostMapping(value = "/addLifecycleEvent")
    public Result<String> addLifecycleEvent(@RequestBody LifecycleEventDto eventDto) {
        if (eventDto == null || eventDto.getAnimalId() == null || eventDto.getEventType() == null) {
            return Result.error("参数不能为空");
        }
        try {
            ahAnimalService.addLifecycleEvent(eventDto);
            return Result.OK("添加事件成功！");
        } catch (Exception e) {
            log.error("添加生命周期事件失败", e);
            return Result.error("添加事件失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取设备最新遥测数据", description = "根据ThingsBoard设备ID获取最新遥测数据")
    @GetMapping("/get-latest-telemetry/{tbDeviceId}")
    public Result<?> getLatestTelemetry(@PathVariable("tbDeviceId") String tbDeviceId) {
        if (tbDeviceId == null || "null".equals(tbDeviceId) || tbDeviceId.trim().isEmpty()) {
            return Result.error("设备ID为空，无法查询遥测数据");
        }
        return ahThingsBoardDeviceService.getLatestTelemetry(tbDeviceId);
    }

    @Autowired
    private TDengineTimeSeriesServiceImpl tdengineService;

    @Operation(summary = "获取设备时序遥测数据", description = "根据设备ID和遥测键获取时间序列数据, 用于绘制图表")
    @GetMapping("/get-timeseries-telemetry")
    public Result<?> getTimeSeriesTelemetry(
            @RequestParam("deviceId") String deviceId,
            @RequestParam("key") String key,
            @RequestParam("startTs") Long startTs,
            @RequestParam("endTs") Long endTs) {

        if (deviceId == null || key == null || startTs == null || endTs == null) {
            return Result.error("参数不完整");
        }
        
        log.info("获取牲畜时序数据: deviceId={}, key={}, start={}, end={}", deviceId, key, startTs, endTs);
        
        try {
            List<Map<String, Object>> result = tdengineService.getTelemetryHistoryForKey(deviceId, key, startTs, endTs);
            
            // 转换为ECharts格式 [timestamp, value]
            List<List<Object>> chartData = result.stream()
                .map(item -> Arrays.asList(item.get("ts"), item.get("value")))
                .collect(Collectors.toList());
                
            return Result.OK(chartData);
        } catch (Exception e) {
            log.error("获取时序数据失败", e);
            return Result.error("获取时序数据失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取原始遥测日志", description = "根据设备ID获取原始遥测JSON日志")
    @GetMapping("/get-raw-telemetry-log")
    public Result<?> getRawTelemetryLog(
            @RequestParam("deviceId") String deviceId,
            @RequestParam("startTs") Long startTs,
            @RequestParam("endTs") Long endTs) {

        if (deviceId == null || startTs == null || endTs == null) {
            return Result.error("参数不完整");
        }

        try {
            List<Map<String, Object>> result = tdengineService.getRawTelemetryLog(deviceId, startTs, endTs);
            return Result.OK(result);
        } catch (Exception e) {
            log.error("获取原始遥测日志失败", e);
            return Result.error("获取原始遥测日志失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取可供绑定的牲畜列表", description = "用于设备绑定弹窗中的下拉选择")
    @GetMapping(value = "/listAvailableForBinding")
    public Result<IPage<AhAnimal>> listAvailableForBinding(AhAnimal ahAnimal,
                                                          @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                          @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                          @RequestParam(name="deviceType", required = true) String deviceType,
                                                          HttpServletRequest req) {
        Page<AhAnimal> page = new Page<>(pageNo, pageSize);
        // 【修正】为防止QueryGenerator将deviceType作为ah_animal表的查询条件，需手动从请求参数中移除
        Map<String, String[]> parameterMap = new HashMap<>(req.getParameterMap());
        parameterMap.remove("deviceType");
        QueryWrapper<AhAnimal> queryWrapper = QueryGenerator.initQueryWrapper(ahAnimal, parameterMap);
        
        IPage<AhAnimal> pageList = ahAnimalService.listAvailableForBinding(page, queryWrapper, deviceType);
        return Result.OK(pageList);
    }
} 