package org.jeecg.modules.animalhusbandry.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.animalhusbandry.entity.AhAnimal;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.animalhusbandry.vo.DashboardKpiVo;
import org.jeecg.modules.animalhusbandry.vo.AhDeviceVo;

import java.util.List;
import java.util.Map;

/**
 * @Description: 牲畜档案表
 * @Author: jeecg-boot
 * @Date:   2024-08-26
 * @Version: V1.0
 */
public interface AhAnimalMapper extends BaseMapper<AhAnimal> {
    /**
     * 按健康状态分组统计牲畜数量
     * @return List<Map<String, Object>> 每个Map包含 health_status 和 count
     */
    List<Map<String, Object>> countByHealthStatus();

    /**
     * 根据一批牲畜ID，查询它们所有有效的绑定设备信息
     * @param animalIds 牲畜ID列表
     * @return List<AhDeviceVo> 包含animalId用于分组
     */
    List<AhDeviceVo> queryBoundDevicesByAnimalIds(@Param("animalIds") List<String> animalIds);
} 