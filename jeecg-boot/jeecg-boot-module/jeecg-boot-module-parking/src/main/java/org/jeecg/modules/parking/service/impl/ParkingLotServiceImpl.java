package org.jeecg.modules.parking.service.impl;

import org.jeecg.modules.parking.entity.ParkingLot;
import org.jeecg.modules.parking.entity.ParkingLotConfig;
import org.jeecg.modules.parking.mapper.ParkingLotConfigMapper;
import org.jeecg.modules.parking.mapper.ParkingLotMapper;
import org.jeecg.modules.parking.service.IParkingLotService;
import org.jeecg.modules.parking.vo.ParkingLotPage;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Collection;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.parking.entity.ParkingRecord;
import org.jeecg.modules.parking.mapper.ParkingRecordMapper;
import org.springframework.beans.BeanUtils;
import javax.annotation.Resource;

/**
 * @Description: 停车场核心信息表
 * @Author: jeecg-boot
 * @Date:   2024-08-10
 * @Version: V1.0
 */
@Service
public class ParkingLotServiceImpl extends ServiceImpl<ParkingLotMapper, ParkingLot> implements IParkingLotService {

	@Autowired
	private ParkingLotMapper parkingLotMapper;
	@Autowired
	private ParkingLotConfigMapper parkingLotConfigMapper;
	
	// 注入停车记录的Mapper，用于查询在场车辆数
	@Resource
	private ParkingRecordMapper parkingRecordMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(ParkingLotPage parkingLotPage) {
		parkingLotMapper.insert(parkingLotPage);
		ParkingLotConfig parkingLotConfig = parkingLotPage.getParkingLotConfig();
		if(parkingLotConfig !=null) {
			parkingLotConfig.setParkingLotId(parkingLotPage.getId());
			parkingLotConfigMapper.insert(parkingLotConfig);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(ParkingLotPage parkingLotPage) {
		parkingLotMapper.updateById(parkingLotPage);

		ParkingLotConfig parkingLotConfig = parkingLotPage.getParkingLotConfig();
		//1.先删除子表数据
		parkingLotConfigMapper.delete(new LambdaQueryWrapper<ParkingLotConfig>()
			.eq(ParkingLotConfig::getParkingLotId, parkingLotPage.getId()));

		//2.子表数据重新插入
		if(parkingLotConfig !=null) {
			parkingLotConfig.setParkingLotId(parkingLotPage.getId());
			// 确保子表ID为空，以执行插入而非更新
			parkingLotConfig.setId(null);
			parkingLotConfigMapper.insert(parkingLotConfig);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		parkingLotConfigMapper.delete(new LambdaQueryWrapper<ParkingLotConfig>()
			.eq(ParkingLotConfig::getParkingLotId, id));
		parkingLotMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id : idList) {
			parkingLotConfigMapper.delete(new LambdaQueryWrapper<ParkingLotConfig>()
				.eq(ParkingLotConfig::getParkingLotId, id));
			parkingLotMapper.deleteById(id);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public IPage<ParkingLotPage> queryParkingLotPage(Page<ParkingLot> page, QueryWrapper<ParkingLot> queryWrapper) {
		// 1. 先执行标准的分页查询，查出停车场基本信息
		IPage<ParkingLot> lotPage = this.baseMapper.selectPage(page, queryWrapper);

		// 2. 创建一个新的分页对象，用于存放最终结果
		IPage<ParkingLotPage> resultPage = new Page<>(lotPage.getCurrent(), lotPage.getSize(), lotPage.getTotal());

		// 3. 遍历查询结果，为每个停车场填充在场车辆数
		List<ParkingLotPage> pageRecords = lotPage.getRecords().stream().map(parkingLot -> {
			ParkingLotPage pageVo = new ParkingLotPage();
			BeanUtils.copyProperties(parkingLot, pageVo);

			// 4. 查询当前停车场的在场车辆数
			QueryWrapper<ParkingRecord> recordQueryWrapper = new QueryWrapper<>();
			recordQueryWrapper.eq("parking_lot_id", parkingLot.getId());
			recordQueryWrapper.isNull("exit_time"); // 在场车辆，出场时间为空
			
			// 使用 count 方法，性能更高
			Long usedSpaces = parkingRecordMapper.selectCount(recordQueryWrapper);
			pageVo.setUsedSpaces(usedSpaces.intValue());

			return pageVo;
		}).collect(Collectors.toList());
		
		resultPage.setRecords(pageRecords);
		return resultPage;
	}
} 