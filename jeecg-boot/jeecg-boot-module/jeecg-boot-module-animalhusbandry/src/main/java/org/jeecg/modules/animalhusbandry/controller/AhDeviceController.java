package org.jeecg.modules.animalhusbandry.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.animalhusbandry.entity.AhDevice;
import org.jeecg.modules.animalhusbandry.service.IAhDeviceService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.animalhusbandry.vo.AhDevicePage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jeecg.modules.animalhusbandry.vo.AhDeviceSyncDTO;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.animalhusbandry.vo.AhDeviceBindDTO;
import org.jeecg.modules.animalhusbandry.vo.AhDeviceUnbindDTO;
import javax.validation.Valid;
import java.util.List;

 /**
 * @Description: 设备信息表
 * @Author: jeecg-boot
 * @Date:   2024-08-26
 * @Version: V1.0
 */
@Tag(name="设备台账管理")
@RestController
@RequestMapping("/animal_husbandry/device")
@Slf4j
public class AhDeviceController extends JeecgController<AhDevice, IAhDeviceService> {
	@Autowired
	private IAhDeviceService ahDeviceService;

	/**
	 * 分页列表查询
	 *
	 * @param ahDevice
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@Operation(summary="设备台账-分页列表查询", description="设备台账-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<AhDevicePage>> queryPageList(AhDevice ahDevice,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<AhDevice> queryWrapper = QueryGenerator.initQueryWrapper(ahDevice, req.getParameterMap());
		Page<AhDevicePage> page = new Page<>(pageNo, pageSize);
		IPage<AhDevicePage> pageList = ahDeviceService.queryDevicePage(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param ahDevice
	 * @return
	 */
	@Operation(summary="设备台账-添加", description="设备台账-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody AhDevice ahDevice) {
		ahDeviceService.save(ahDevice);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param ahDevice
	 * @return
	 */
	@Operation(summary="设备台账-编辑", description="设备台账-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
	public Result<String> edit(@RequestBody AhDevice ahDevice) {
		ahDeviceService.updateById(ahDevice);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@Operation(summary="设备台账-通过id删除", description="设备台账-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		ahDeviceService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@Operation(summary="设备台账-批量删除", description="设备台账-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.ahDeviceService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@Operation(summary="设备台账-通过id查询", description="设备台账-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<AhDevice> queryById(@RequestParam(name="id",required=true) String id) {
		AhDevice ahDevice = ahDeviceService.getById(id);
		if(ahDevice==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(ahDevice);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param ahDevice
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, AhDevice ahDevice) {
        return super.exportXls(request, ahDevice, AhDevice.class, "设备信息表");
    }

    /**
    * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, AhDevice.class);
    }


    @Operation(summary="从ThingsBoard同步设备", description="从ThingsBoard同步设备")
    @PostMapping("/syncFromThingsboard")
    public Result<?> syncFromThingsboard(@RequestBody AhDeviceSyncDTO syncDTO) {
        try {
            ahDeviceService.syncFromThingsboard(syncDTO);
            return Result.OK("设备同步入库成功！");
        } catch (JeecgBootException e) {
            log.error("设备同步失败：", e);
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("设备同步发生未知错误：", e);
            return Result.error("操作失败，请联系管理员。");
        }
    }

    @Operation(summary="将设备绑定到牲畜", description="将设备绑定到牲畜")
    @PostMapping("/bind")
    public Result<?> bindDeviceToAnimal(@RequestBody AhDeviceBindDTO bindDTO) {
        try {
            ahDeviceService.bindDeviceToAnimal(bindDTO);
            return Result.OK("设备绑定成功！");
        } catch (JeecgBootException e) {
            log.error("设备绑定失败：", e);
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("设备绑定发生未知错误：", e);
            return Result.error("操作失败，请联系管理员。");
        }
    }

    @Operation(summary="将设备从牲畜解绑", description="将设备从牲畜解绑")
    @PostMapping("/unbind")
    public Result<?> unbindFromAnimal(@RequestBody @Valid AhDeviceUnbindDTO unbindDTO) {
        try {
            ahDeviceService.unbindFromAnimal(unbindDTO);
            return Result.OK("解绑成功！");
        } catch (JeecgBootException e) {
            log.error("设备解绑失败：", e);
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("设备解绑发生未知错误：", e);
            return Result.error("操作失败，请联系管理员。");
        }
    }

    @Operation(summary="获取可供绑定的设备列表（根据设备类型）", description="查询所有'库存中'或'闲置'的特定类型设备")
    @GetMapping("/listAvailableForBinding")
    public Result<List<AhDevice>> listAvailableForBinding(@RequestParam(name="deviceType", required = true) String deviceType) {
        QueryWrapper<AhDevice> queryWrapper = new QueryWrapper<AhDevice>()
            .eq("device_type", deviceType)
            .in("status", "IN_STOCK", "IDLE");
        List<AhDevice> list = ahDeviceService.list(queryWrapper);
        return Result.OK(list);
    }

    @Operation(summary="手动变更设备生命周期状态", description="手动变更设备生命周期状态")
    @PostMapping("/changeStatus")
    public Result<?> changeStatus(@RequestBody Object payload) {
        // TODO: 实现设备状态变更逻辑
        log.info("changeStatus payload: {}", payload);
        return Result.OK("状态变更请求已收到");
    }

} 