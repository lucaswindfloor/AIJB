package org.jeecg.modules.parking.service;

import org.jeecg.modules.parking.entity.LockDevice;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 车位锁设备
 * @Author: jeecg-boot
 * @Date:   2025-07-01
 * @Version: V1.0
 */
public interface ILockDeviceService extends IService<LockDevice> {

    /**
     * 控制地锁升降
     * @param deviceId 设备在系统中的ID
     * @param action "up" 或 "down"
     */
    void controlLock(String deviceId, String action);

    /**
     * 从ThingsBoard刷新并更新设备状态
     * @param deviceId 设备在系统中的ID
     */
    void refreshStatus(String deviceId);

} 