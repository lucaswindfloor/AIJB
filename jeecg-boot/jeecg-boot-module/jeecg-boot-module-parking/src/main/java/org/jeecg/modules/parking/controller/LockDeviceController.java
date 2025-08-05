package org.jeecg.modules.parking.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.parking.entity.LockDevice;
import org.jeecg.modules.parking.service.ILockDeviceService;
import org.jeecg.modules.parking.service.IThingsBoardDeviceService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: 车位锁设备
 * @Author: jeecg-boot
 * @Date:   2025-07-01
 * @Version: V1.0
 */
@Tag(name="车位锁设备")
@RestController
@RequestMapping("/parking/lock-device")
@Slf4j
public class LockDeviceController extends JeecgController<LockDevice, ILockDeviceService> {
	@Autowired
	private ILockDeviceService lockDeviceService;
	
	@Autowired
	private IThingsBoardDeviceService thingsBoardDeviceService;

	/**
	 * 分页列表查询
	 *
	 * @param lockDevice
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "车位锁设备-分页列表查询")
	@Operation(summary="车位锁设备-分页列表查询", description="车位锁设备-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<LockDevice>> queryPageList(LockDevice lockDevice,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<LockDevice> queryWrapper = QueryGenerator.initQueryWrapper(lockDevice, req.getParameterMap());
		Page<LockDevice> page = new Page<LockDevice>(pageNo, pageSize);
		IPage<LockDevice> pageList = lockDeviceService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 * 升锁
	 */
	@AutoLog(value = "车位锁-升锁")
	@Operation(summary = "车位锁-升锁", description = "向ThingsBoard发送升锁指令")
	@GetMapping(value = "/lock-up")
	public Result<?> lockUp(@RequestParam("deviceId") String deviceId) {
		lockDeviceService.controlLock(deviceId, "up");
		return Result.OK("升锁指令已发送");
	}

	/**
	 * 降锁
	 */
	@AutoLog(value = "车位锁-降锁")
	@Operation(summary = "车位锁-降锁", description = "向ThingsBoard发送降锁指令")
	@GetMapping(value = "/lock-down")
	public Result<?> lockDown(@RequestParam("deviceId") String deviceId) {
		lockDeviceService.controlLock(deviceId, "down");
		return Result.OK("降锁指令已发送");
	}

	/**
	 * 从ThingsBoard刷新设备状态
	 */
	@AutoLog(value = "车位锁-刷新状态")
	@Operation(summary = "车位锁-刷新状态", description = "从ThingsBoard主动获取最新设备状态")
	@GetMapping(value = "/refresh-status")
	public Result<?> refreshStatus(@RequestParam("deviceId") String deviceId) {
		lockDeviceService.refreshStatus(deviceId);
		return Result.OK("刷新成功");
	}

	/**
	 *   添加
	 *
	 * @param lockDevice
	 * @return
	 */
	@AutoLog(value = "车位锁设备-添加")
	@Operation(summary="车位锁设备-添加", description="车位锁设备-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody LockDevice lockDevice) {
		lockDeviceService.save(lockDevice);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param lockDevice
	 * @return
	 */
	@AutoLog(value = "车位锁设备-编辑")
	@Operation(summary="车位锁设备-编辑", description="车位锁设备-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody LockDevice lockDevice) {
		lockDeviceService.updateById(lockDevice);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "车位锁设备-通过id删除")
	@Operation(summary="车位锁设备-通过id删除", description="车位锁设备-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		lockDeviceService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "车位锁设备-批量删除")
	@Operation(summary="车位锁设备-批量删除", description="车位锁设备-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.lockDeviceService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "车位锁设备-通过id查询")
	@Operation(summary="车位锁设备-通过id查询", description="车位锁设备-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<LockDevice> queryById(@RequestParam(name="id",required=true) String id) {
		LockDevice lockDevice = lockDeviceService.getById(id);
		if(lockDevice==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(lockDevice);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param lockDevice
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, LockDevice lockDevice) {
        return super.exportXls(request, lockDevice, LockDevice.class, "车位锁设备");
    }

} 