package com.moldai.service;

/**
 * 设备控制服务接口
 * 负责向物联网平台下发控制指令
 */
public interface IDeviceControlService {

    /**
     * 发送单向RPC指令控制设备
     * 
     * @param deviceId 设备ID
     * @param method 方法名 (e.g. "setVentilation")
     * @param params 参数 (JSON字符串 or boolean/int)
     * @return 是否发送成功
     */
    boolean sendRpcCommand(String deviceId, String method, Object params);
}





