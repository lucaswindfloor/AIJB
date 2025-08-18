package org.jeecg.modules.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.jeecg.modules.ai.entity.AhAnimal;

/**
 * @Description: 牲畜健康数据访问接口 (AI模块私有)
 * @Author: AI Assistant
 * @Date: 2024-08-24
 * @Version: V1.0
 */
@Mapper
public interface AnimalHealthMapper extends BaseMapper<AhAnimal> {

}
