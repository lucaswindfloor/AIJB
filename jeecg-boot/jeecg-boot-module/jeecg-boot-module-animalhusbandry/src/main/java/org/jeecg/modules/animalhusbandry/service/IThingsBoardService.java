package org.jeecg.modules.animalhusbandry.service;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.animalhusbandry.dto.RpcCommandDto;

import java.util.Map;

/**
 * @Description: ThingsBoard平台交互服务接口
 * @Author: Gemini
 * @Date:   2024-08-23
 * @Version: V1.0
 */
public interface IThingsBoardService {

    /**
     * 向ThingsBoard管理的设备发送RPC请求
     * @param tbDeviceId ThingsBoard平台中的设备ID
     * @param commandDto 指令内容
     * @return ThingsBoard平台的响应
     */
    Result<?> sendRpc(String tbDeviceId, RpcCommandDto commandDto);

    /**
     * 根据DevEUI从ThingsBoard获取设备详情
     * @param devEui 设备的DevEUI
     * @return 包含设备详情的Map
     */
    Map<String, Object> getDeviceDetailsByDevEUI(String devEui);

} 