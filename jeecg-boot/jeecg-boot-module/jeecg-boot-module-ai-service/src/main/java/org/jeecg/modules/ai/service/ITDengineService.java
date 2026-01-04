package org.jeecg.modules.ai.service;

import java.util.List;
import java.util.Map;

/**
 * @Description: TDengine数据服务接口
 * @Author: AI Assistant
 * @Date:   2024-08-24
 * @Version: V1.0
 */
public interface ITDengineService {

    /**
     * 根据设备EUI和时间范围，查询生理数据
     * @param devEui 设备EUI (对应TDengine中的子表名后缀)
     * @param hoursAgo 小时数，查询从现在起过去多少小时的数据
     * @return
     */
    List<Map<String, Object>> queryPhysiologicalData(String devEui, int hoursAgo);

}
