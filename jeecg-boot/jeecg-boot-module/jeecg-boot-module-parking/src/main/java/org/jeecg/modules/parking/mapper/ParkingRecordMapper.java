package org.jeecg.modules.parking.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.parking.entity.ParkingRecord;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.parking.vo.ParkingRecordPage;

/**
 * @Description: 停车记录表
 * @Author: jeecg-boot
 * @Date:   2024-08-12
 * @Version: V1.0
 */
public interface ParkingRecordMapper extends BaseMapper<ParkingRecord> {

    /**
     * 自定义分页查询，包含关联表信息
     * @param page
     * @param queryWrapper
     * @return
     */
    IPage<ParkingRecordPage> getParkingRecordPage(Page<ParkingRecordPage> page, @Param("ew") QueryWrapper<ParkingRecord> queryWrapper);

} 