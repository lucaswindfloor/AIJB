package org.jeecg.modules.animalhusbandry.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.animalhusbandry.entity.AhAnimal;
import org.jeecg.modules.animalhusbandry.entity.AhAnimalDeviceLink;
import org.jeecg.modules.animalhusbandry.entity.AhDevice;
import org.jeecg.modules.animalhusbandry.mapper.AhDeviceMapper;
import org.jeecg.modules.animalhusbandry.service.IAhAnimalDeviceLinkService;
import org.jeecg.modules.animalhusbandry.service.IAhAnimalService;
import org.jeecg.modules.animalhusbandry.service.IAhDeviceService;
import org.jeecg.modules.animalhusbandry.service.IThingsBoardService;
import org.jeecg.modules.animalhusbandry.vo.AhDeviceBindDTO;
import org.jeecg.modules.animalhusbandry.vo.AhDeviceSyncDTO;
import org.jeecg.modules.animalhusbandry.vo.AhDevicePage;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.animalhusbandry.vo.AhDeviceUnbindDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @Description: 设备信息表
 * @Author: jeecg-boot
 * @Date:   2024-08-26
 * @Version: V1.0
 */
@Service
public class AhDeviceServiceImpl extends ServiceImpl<AhDeviceMapper, AhDevice> implements IAhDeviceService {

    @Autowired
    private AhDeviceMapper ahDeviceMapper;

    @Autowired
    private IThingsBoardService thingsBoardService;

    @Autowired
    private IAhAnimalService ahAnimalService;

    @Autowired
    private IAhAnimalDeviceLinkService ahAnimalDeviceLinkService;

    @Override
    public IPage<AhDevicePage> queryDevicePage(Page<AhDevicePage> page, QueryWrapper<AhDevice> queryWrapper) {
        return ahDeviceMapper.getDevicePage(page, queryWrapper);
    }

    @Override
    public void syncFromThingsboard(AhDeviceSyncDTO syncDTO) {
        // 1. 检查本地数据库，确认该 devEui 是否已存在
        long count = this.count(new QueryWrapper<AhDevice>().eq("dev_eui", syncDTO.getDevEui()));
        if (count > 0) {
            throw new JeecgBootException("同步失败：该DevEUI的设备已存在于系统中。");
        }

        // 2. 调用 IThingsBoardService 获取设备详情
        Map<String, Object> tbDetails = thingsBoardService.getDeviceDetailsByDevEUI(syncDTO.getDevEui());
        if (tbDetails == null) {
            throw new JeecgBootException("同步失败：无法从ThingsBoard平台获取该DevEUI的设备信息。");
        }

        // 3. 合并信息，创建新的 Device 实体
        AhDevice newDevice = new AhDevice();
        newDevice.setDevEui(syncDTO.getDevEui());
        newDevice.setName(syncDTO.getName());
        newDevice.setDeviceType(syncDTO.getDeviceType());
        newDevice.setModel(syncDTO.getModel());
        newDevice.setPurchaseDate(syncDTO.getPurchaseDate());

        // 从模拟的TB服务获取信息
        newDevice.setTbDeviceId((String) tbDetails.get("tb_device_id"));

        // 4. 设置初始状态为“库存中”
        newDevice.setStatus("IN_STOCK");

        // 5. 保存到数据库
        this.save(newDevice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindDeviceToAnimal(AhDeviceBindDTO bindDTO) {
        // 1. 查询设备和牲畜实体，并进行校验
        AhDevice device = this.getById(bindDTO.getDeviceId());
        if (device == null) {
            throw new JeecgBootException("绑定失败：设备不存在。");
        }
        // 只有“库存中”或“闲置”的设备才能被绑定
        if (!Arrays.asList("IN_STOCK", "IDLE").contains(device.getStatus())) {
            throw new JeecgBootException("绑定失败：只有'库存中'或'闲置'状态的设备才能被绑定。");
        }

        AhAnimal animal = ahAnimalService.getById(bindDTO.getAnimalId());
        if (animal == null) {
            throw new JeecgBootException("绑定失败：牲畜不存在。");
        }

        // 2. 检查该设备是否已有未解绑的有效关联
        long activeLinks = ahAnimalDeviceLinkService.count(
            new QueryWrapper<AhAnimalDeviceLink>()
                .eq("device_id", bindDTO.getDeviceId())
                .eq("is_active", 1)
        );
        if (activeLinks > 0) {
            throw new JeecgBootException("绑定失败：该设备已被其他牲畜绑定。");
        }
        
        // （可选）检查该牲畜是否已绑定同类型的设备，防止一个牲畜绑定两个追踪器
        
        // 3. 在 ah_animal_device_link 表中创建新的有效记录
        AhAnimalDeviceLink newLink = new AhAnimalDeviceLink();
        newLink.setDeviceId(bindDTO.getDeviceId());
        newLink.setAnimalId(bindDTO.getAnimalId());
        newLink.setDeviceType(device.getDeviceType()); // 保存设备类型
        newLink.setBindTime(new Date());
        newLink.setIsActive(1);
        ahAnimalDeviceLinkService.save(newLink);

        // 4. 更新 ah_device 表，将设备状态设为“在用”
        device.setStatus("ACTIVE");
        this.updateById(device);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbindFromAnimal(AhDeviceUnbindDTO unbindDTO) {
        // 1. 查询设备实体，并进行校验
        AhDevice device = this.getById(unbindDTO.getDeviceId());
        if (device == null) {
            throw new JeecgBootException("解绑失败：设备不存在。");
        }
        if (!"ACTIVE".equals(device.getStatus())) {
            throw new JeecgBootException("解绑失败：只有'在用'状态的设备才能被解绑。");
        }

        // 2. 查找当前有效的绑定记录
        AhAnimalDeviceLink activeLink = ahAnimalDeviceLinkService.getOne(
            new QueryWrapper<AhAnimalDeviceLink>()
                .eq("device_id", unbindDTO.getDeviceId())
                .eq("is_active", 1)
        );

        if (activeLink == null) {
            throw new JeecgBootException("解绑失败：未找到有效的设备绑定记录。");
        }

        // 3. 更新绑定记录：设为无效，并记录解绑时间
        activeLink.setIsActive(0);
        activeLink.setUnbindTime(new Date());
        ahAnimalDeviceLinkService.updateById(activeLink);

        // 4. 更新设备状态：改回“闲置”
        device.setStatus("IDLE");
        this.updateById(device);
    }
} 