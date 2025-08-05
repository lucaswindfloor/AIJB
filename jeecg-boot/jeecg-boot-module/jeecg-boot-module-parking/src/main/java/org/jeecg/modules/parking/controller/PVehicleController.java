package org.jeecg.modules.parking.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.parking.entity.PVehicle;
import org.jeecg.modules.parking.service.IPVehicleService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

 /**
 * @Description: 车辆信息表
 * @Author: jeecg-boot
 * @Date:   2024-08-12
 * @Version: V1.1
 */
@Tag(name="车辆信息表")
@RestController
@RequestMapping("/parking/pVehicle")
@Slf4j
public class PVehicleController extends JeecgController<PVehicle, IPVehicleService> {
	@Autowired
	private IPVehicleService pVehicleService;
	
	/**
	 * 分页列表查询
	 *
	 * @param pVehicle
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@Operation(summary="分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<PVehicle>> queryPageList(PVehicle pVehicle,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<PVehicle> queryWrapper = QueryGenerator.initQueryWrapper(pVehicle, req.getParameterMap());
		Page<PVehicle> page = new Page<PVehicle>(pageNo, pageSize);
		IPage<PVehicle> pageList = pVehicleService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param pVehicle
	 * @return
	 */
	@Operation(summary="添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody PVehicle pVehicle) {
		pVehicleService.save(pVehicle);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param pVehicle
	 * @return
	 */
	@Operation(summary="编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody PVehicle pVehicle) {
		pVehicleService.updateById(pVehicle);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@Operation(summary="通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		pVehicleService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@Operation(summary="批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.pVehicleService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@Operation(summary="通过id查询")
	@GetMapping(value = "/queryById")
	public Result<PVehicle> queryById(@RequestParam(name="id",required=true) String id) {
		PVehicle pVehicle = pVehicleService.getById(id);
		if(pVehicle==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(pVehicle);
	}
	
    /**
    * 导出excel
    *
    * @param request
    * @param pVehicle
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, PVehicle pVehicle) {
        return super.exportXls(request, pVehicle, PVehicle.class, "车辆信息表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @PostMapping(value = "importExcel")
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, PVehicle.class);
    }

	/**
	 * 审核车辆
	 * @param pVehicle
	 * @return
	 */
	@Operation(summary="审核车辆")
	@PostMapping(value = "/approve")
	public Result<String> approve(@RequestBody PVehicle pVehicle) {
		PVehicle vehicleToUpdate = new PVehicle();
		vehicleToUpdate.setId(pVehicle.getId());
		vehicleToUpdate.setStatus(pVehicle.getStatus());
		pVehicleService.updateById(vehicleToUpdate);
		return Result.OK("审核成功！");
	}
} 