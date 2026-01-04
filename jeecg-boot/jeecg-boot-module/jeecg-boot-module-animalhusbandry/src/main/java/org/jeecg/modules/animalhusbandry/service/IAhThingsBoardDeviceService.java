package org.jeecg.modules.animalhusbandry.service;

import org.jeecg.common.api.vo.Result;

/**
 * 与ThingsBoard交互的【畜牧业】设备服务
 */
public interface IAhThingsBoardDeviceService {

    /**
     * 根据ThingsBoard设备ID获取最新遥测数据
     * @param tbDeviceId ThingsBoard设备ID
     * @return 遥测数据
     */
    Result<?> getLatestTelemetry(String tbDeviceId);
} 