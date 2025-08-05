package org.jeecg.modules.parking.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.parking.entity.Checkpoint;
import org.jeecg.modules.parking.service.ICheckpointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

 /**
 * @Description: 关卡/出入口表
 * @Author: jeecg-boot
 * @Date:   2024-08-10
 * @Version: V1.0
 */
@Tag(name="关卡/出入口表")
@RestController
@RequestMapping("/parking/checkpoint")
@Slf4j
public class CheckpointController extends JeecgController<Checkpoint, ICheckpointService> {
	@Autowired
	private ICheckpointService checkpointService;

	/**
	 * 分页列表查询
	 *
	 * @param checkpoint
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@Operation(summary="关卡/出入口表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<Checkpoint>> queryPageList(Checkpoint checkpoint,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<Checkpoint> queryWrapper = QueryGenerator.initQueryWrapper(checkpoint, req.getParameterMap());
		Page<Checkpoint> page = new Page<Checkpoint>(pageNo, pageSize);
		IPage<Checkpoint> pageList = checkpointService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param checkpoint
	 * @return
	 */
	@Operation(summary="关卡/出入口表-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody Checkpoint checkpoint) {
		checkpointService.save(checkpoint);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param checkpoint
	 * @return
	 */
	@Operation(summary="关卡/出入口表-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody Checkpoint checkpoint) {
		checkpointService.updateById(checkpoint);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@Operation(summary="关卡/出入口表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		checkpointService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@Operation(summary="关卡/出入口表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.checkpointService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@Operation(summary="关卡/出入口表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<Checkpoint> queryById(@RequestParam(name="id",required=true) String id) {
		Checkpoint checkpoint = checkpointService.getById(id);
		if(checkpoint==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(checkpoint);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param checkpoint
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, Checkpoint checkpoint) {
      return super.exportXls(request, checkpoint, Checkpoint.class, "关卡/出入口表");
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
      return super.importExcel(request, response, Checkpoint.class);
  }

} 