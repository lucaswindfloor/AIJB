package org.jeecg.modules.parking.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.parking.entity.ChargingRule;
import org.jeecg.modules.parking.service.IChargingRuleService;
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
 * @Description: 计费规则表
 * @Author: jeecg-boot
 * @Date:   2024-08-10
 * @Version: V1.0
 */
@Tag(name="计费规则表")
@RestController
@RequestMapping("/parking/chargingRule")
@Slf4j
public class ChargingRuleController extends JeecgController<ChargingRule, IChargingRuleService> {
	@Autowired
	private IChargingRuleService chargingRuleService;

	/**
	 * 分页列表查询
	 *
	 * @param chargingRule
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@Operation(summary ="计费规则表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ChargingRule>> queryPageList(ChargingRule chargingRule,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<ChargingRule> queryWrapper = QueryGenerator.initQueryWrapper(chargingRule, req.getParameterMap());
		Page<ChargingRule> page = new Page<ChargingRule>(pageNo, pageSize);
		IPage<ChargingRule> pageList = chargingRuleService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param chargingRule
	 * @return
	 */
	@Operation(summary ="计费规则表-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ChargingRule chargingRule) {
		chargingRuleService.save(chargingRule);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param chargingRule
	 * @return
	 */
	@Operation(summary ="计费规则表-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ChargingRule chargingRule) {
		chargingRuleService.updateById(chargingRule);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@Operation(summary ="计费规则表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		chargingRuleService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@Operation(summary ="计费规则表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.chargingRuleService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@Operation(summary ="计费规则表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ChargingRule> queryById(@RequestParam(name="id",required=true) String id) {
		ChargingRule chargingRule = chargingRuleService.getById(id);
		if(chargingRule==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(chargingRule);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param chargingRule
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, ChargingRule chargingRule) {
      return super.exportXls(request, chargingRule, ChargingRule.class, "计费规则表");
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
      return super.importExcel(request, response, ChargingRule.class);
  }

} 