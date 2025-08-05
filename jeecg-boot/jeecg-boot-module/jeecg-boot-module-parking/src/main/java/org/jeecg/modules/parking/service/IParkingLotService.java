package org.jeecg.modules.parking.service;

import org.jeecg.modules.parking.entity.ParkingLotConfig;
import org.jeecg.modules.parking.entity.ParkingLot;
import org.jeecg.modules.parking.vo.ParkingLotPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @Description: 停车场核心信息表
 * @Author: jeecg-boot
 * @Date:   2024-08-10
 * @Version: V1.0
 */
public interface IParkingLotService extends IService<ParkingLot> {

	/**
	 * 新增
	 * @param parkingLotPage
	 */
	public void saveMain(ParkingLotPage parkingLotPage);

	/**
	 * 修改
	 * @param parkingLotPage
	 */
	public void updateMain(ParkingLotPage parkingLotPage);

	/**
	 * 删除
	 * @param id
	 */
	public void delMain(String id);

	/**
	 * 批量删除
	 * @param idList
	 */
	public void delBatchMain(java.util.Collection<? extends java.io.Serializable> idList);

	/**
	 * 自定义分页查询，包含在场车辆数
	 * @param page 分页对象
	 * @param queryWrapper 查询构造器
	 * @return IPage<ParkingLotPage>
	 */
	IPage<ParkingLotPage> queryParkingLotPage(Page<ParkingLot> page, QueryWrapper<ParkingLot> queryWrapper);

} 