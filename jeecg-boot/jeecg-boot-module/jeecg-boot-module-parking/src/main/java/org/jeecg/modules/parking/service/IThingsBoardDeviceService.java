package org.jeecg.modules.parking.service;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.parking.entity.LockDevice;

/**
 * @Description: 与ThingsBoard平台交互的服务
 * @Author: AI Assistant
 * @Date:   2025-07-01
 * @Version: V1.0
 */
public interface IThingsBoardDeviceService {

    /**
     * 向ThingsBoard发送RPC控制命令
     * @param lockDevice 本地数据库中的设备实体
     * @param command 命令类型 (e.g., LOCK_UP, LOCK_DOWN)
     * @param reason 操作原因，用于日志记录
     * @return 操作结果
     */
    Result<?> sendControlCommand(LockDevice lockDevice, String command, String reason);

} 