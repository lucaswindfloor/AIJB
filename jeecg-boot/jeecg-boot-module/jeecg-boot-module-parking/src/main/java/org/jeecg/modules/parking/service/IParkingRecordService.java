package org.jeecg.modules.parking.service;

import org.jeecg.modules.parking.entity.ParkingRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.parking.vo.ParkingRecordPage;

import java.util.Map;

/**
 * @Description: 停车记录表
 * @Author: jeecg-boot
 * @Date:   2024-08-12
 * @Version: V1.0
 */
public interface IParkingRecordService extends IService<ParkingRecord> {

    /**
     * 自定义分页查询，处理复杂查询条件
     * @param page 分页对象
     * @param params 前端传递的查询参数
     * @return IPage<ParkingRecordPage>
     */
    IPage<ParkingRecordPage> queryParkingRecordPage(Page<ParkingRecordPage> page, Map<String, String[]> params);
} 