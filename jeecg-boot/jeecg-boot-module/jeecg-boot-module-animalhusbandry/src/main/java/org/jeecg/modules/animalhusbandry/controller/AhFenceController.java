package org.jeecg.modules.animalhusbandry.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.animalhusbandry.entity.AhFence;
import org.jeecg.modules.animalhusbandry.service.IAhFenceService;

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
 * @Description: 电子围栏表
 * @Author: jeecg-boot
 * @Date:   2024-08-25
 * @Version: V1.0
 */
@Tag(name="电子围栏表")
@RestController
@RequestMapping("/animal_husbandry/ahFence")
@Slf4j
public class AhFenceController extends JeecgController<AhFence, IAhFenceService> {
	@Autowired
	private IAhFenceService ahFenceService;
	
	/**
	 * 分页列表查询
	 *
	 * @param ahFence
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@Operation(summary="电子围栏表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<AhFence>> queryPageList(AhFence ahFence,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<AhFence> queryWrapper = QueryGenerator.initQueryWrapper(ahFence, req.getParameterMap());
		Page<AhFence> page = new Page<AhFence>(pageNo, pageSize);
		IPage<AhFence> pageList = ahFenceService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param ahFence
	 * @return
	 */
	@Operation(summary="电子围栏表-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody AhFence ahFence) {
		ahFenceService.save(ahFence);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param ahFence
	 * @return
	 */
	@Operation(summary="电子围栏表-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody AhFence ahFence) {
		ahFenceService.updateById(ahFence);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@Operation(summary="电子围lerei表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		ahFenceService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@Operation(summary="电子围栏表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.ahFenceService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@Operation(summary="电子围栏表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<AhFence> queryById(@RequestParam(name="id",required=true) String id) {
		AhFence ahFence = ahFenceService.getById(id);
		if(ahFence==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(ahFence);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param ahFence
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, AhFence ahFence) {
        return super.exportXls(request, ahFence, AhFence.class, "电子围栏表");
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
        return super.importExcel(request, response, AhFence.class);
    }

}

