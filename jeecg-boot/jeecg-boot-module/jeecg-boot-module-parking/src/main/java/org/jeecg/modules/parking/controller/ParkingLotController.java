package org.jeecg.modules.parking.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.parking.entity.ParkingLotConfig;
import org.jeecg.modules.parking.entity.ParkingLot;
import org.jeecg.modules.parking.service.IParkingLotService;
import org.jeecg.modules.parking.service.IParkingLotConfigService;
import org.jeecg.modules.parking.vo.ParkingLotPage;
import org.springframework.beans.BeanUtils;
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
 * @Description: 停车场核心信息表
 * @Author: jeecg-boot
 * @Date:   2024-08-10
 * @Version: V1.0
 */
@Tag(name="停车场核心信息表")
@RestController
@RequestMapping("/parking/parkingLot")
@Slf4j
public class ParkingLotController extends JeecgController<ParkingLot, IParkingLotService> {
	@Autowired
	private IParkingLotService parkingLotService;

	@Autowired
	private IParkingLotConfigService parkingLotConfigService;

	/**
	 * 分页列表查询
	 *
	 * @param parkingLot
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@Operation(summary="停车场核心信息表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ParkingLotPage>> queryPageList(ParkingLot parkingLot,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		// 1. 获取原始的查询构造器
		QueryWrapper<ParkingLot> queryWrapper = QueryGenerator.initQueryWrapper(parkingLot, req.getParameterMap());
		
		// 2. 获取我们自定义的查询参数
		String queryStatus = req.getParameter("queryStatus");

		// 3. 如果自定义参数存在，则进行特殊处理
		if (oConvertUtils.isNotEmpty(queryStatus)) {
			String conditionSql = "";
			switch (queryStatus) {
				case "OPERATING":
					// 正常运营: 状态为NORMAL 且 车位未满
					queryWrapper.eq("status", "NORMAL");
					// 注意: 这依赖于 Service 层或 Mapper 层对 usedSpaces 的计算
					// 在Service层实现，这里先构造条件
					conditionSql = "(select count(1) from p_parking_record pr where pr.parking_lot_id = p_parking_lot.id and pr.exit_time is null) < total_spaces";
					break;
				case "FULL":
					// 车位已满: 状态为NORMAL 且 车位已满
					queryWrapper.eq("status", "NORMAL");
					conditionSql = "(select count(1) from p_parking_record pr where pr.parking_lot_id = p_parking_lot.id and pr.exit_time is null) >= total_spaces";
					break;
				case "CLOSED":
					// 暂停营业: 状态为CLOSED
					queryWrapper.eq("status", "CLOSED");
					break;
			}
			if (oConvertUtils.isNotEmpty(conditionSql)) {
				// 使用 apply 而不是 aql，以避免SQL注入风险
				queryWrapper.apply(conditionSql);
			}
		}

		// 4. 执行分页查询
		Page<ParkingLot> page = new Page<>(pageNo, pageSize);
		// !!重要: parkingLotService.page(page, queryWrapper) 返回的是 IPage<ParkingLot>
		// 我们需要一个能返回 IPage<ParkingLotPage> 的新方法，并在其中填充 usedSpaces
		// 这里我们调用一个假设存在的新方法，您需要在 Service 层实现它
		IPage<ParkingLotPage> pageList = parkingLotService.queryParkingLotPage(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param parkingLotPage
	 * @return
	 */
	@Operation(summary="停车场核心信息表-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody ParkingLotPage parkingLotPage) {
		parkingLotService.saveMain(parkingLotPage);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param parkingLotPage
	 * @return
	 */
	@Operation(summary="停车场核心信息表-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody ParkingLotPage parkingLotPage) {
		parkingLotService.updateMain(parkingLotPage);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@Operation(summary="停车场核心信息表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		parkingLotService.delMain(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@Operation(summary="停车场核心信息表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.parkingLotService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@Operation(summary="停车场核心信息表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ParkingLotPage> queryById(@RequestParam(name="id",required=true) String id) {
		ParkingLot parkingLot = parkingLotService.getById(id);
		ParkingLotConfig parkingLotConfig = parkingLotConfigService.getById(id);

        ParkingLotPage parkingLotPage = new ParkingLotPage();
        BeanUtils.copyProperties(parkingLot, parkingLotPage);
        parkingLotPage.setParkingLotConfig(parkingLotConfig);

		return Result.OK(parkingLotPage);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param parkingLot
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, ParkingLot parkingLot) {
      return super.exportXls(request, parkingLot, ParkingLot.class, "停车场核心信息表");
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
      return super.importExcel(request, response, ParkingLot.class);
  }

} 