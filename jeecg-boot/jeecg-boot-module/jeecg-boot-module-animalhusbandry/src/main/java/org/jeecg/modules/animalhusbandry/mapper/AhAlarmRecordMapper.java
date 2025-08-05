package org.jeecg.modules.animalhusbandry.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.animalhusbandry.entity.AhAlarmRecord;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.animalhusbandry.vo.RecentAlarmVo;

import java.util.List;

/**
 * @Description: 告警记录表
 * @Author: jeecg-boot
 * @Date:   2024-08-26
 * @Version: V1.0
 */
public interface AhAlarmRecordMapper extends BaseMapper<AhAlarmRecord> {

    /**
     * 获取最新的N条告警记录
     * @param limit 限制数量
     * @return
     */
    List<RecentAlarmVo> getRecentAlarms(@Param("limit") int limit);

    /**
     * 统计今天新增的告警数量
     * @return
     */
    int countNewAlarmsToday();
} 