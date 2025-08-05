package org.jeecg.modules.animalhusbandry.service;

import org.jeecg.modules.animalhusbandry.entity.AhDevice;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.animalhusbandry.vo.AhDeviceBindDTO;
import org.jeecg.modules.animalhusbandry.vo.AhDeviceSyncDTO;
import org.jeecg.modules.animalhusbandry.vo.AhDevicePage;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.modules.animalhusbandry.vo.AhDeviceUnbindDTO;

/**
 * @Description: 设备信息表
 * @Author: jeecg-boot
 * @Date:   2024-08-26
 * @Version: V1.0
 */
public interface IAhDeviceService extends IService<AhDevice> {

    /**
     * 自定义分页查询，关联牲畜信息
     * @param page
     * @param queryWrapper
     * @return
     */
    IPage<AhDevicePage> queryDevicePage(Page<AhDevicePage> page, QueryWrapper<AhDevice> queryWrapper);

    /**
     * 从ThingsBoard同步设备信息，并创建新设备记录
     * @param syncDTO 包含DevEUI和补充业务信息的DTO
     */
    void syncFromThingsboard(AhDeviceSyncDTO syncDTO);

    /**
     * 将设备绑定到牲畜
     * @param bindDTO 包含deviceId和animalId的DTO
     */
    void bindDeviceToAnimal(AhDeviceBindDTO bindDTO);

    /**
     * 将设备从牲畜解绑
     * @param unbindDTO 包含deviceId的DTO
     */
    void unbindFromAnimal(AhDeviceUnbindDTO unbindDTO);
} 