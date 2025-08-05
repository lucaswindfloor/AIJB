package org.jeecg.modules.animalhusbandry.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.animalhusbandry.entity.AhAnimal;
import org.jeecg.modules.animalhusbandry.entity.AhAnimalDeviceLink;
import org.jeecg.modules.animalhusbandry.entity.AhAnimalLifecycleEvent;
import org.jeecg.modules.animalhusbandry.entity.AhDevice;
import org.jeecg.modules.animalhusbandry.entity.AhAlarmRecord;
import org.jeecg.modules.animalhusbandry.mapper.*;
import org.jeecg.modules.animalhusbandry.service.IAhAnimalService;
import org.jeecg.modules.animalhusbandry.service.IAhThingsBoardDeviceService;
import org.jeecg.modules.animalhusbandry.vo.AhAnimalDetailVo;
import org.jeecg.modules.animalhusbandry.vo.AhDeviceVo;
import org.jeecg.modules.animalhusbandry.vo.LifecycleEventDto;
import org.jeecg.modules.animalhusbandry.vo.AhAnimalPageVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.HashMap;

@Service
public class AhAnimalServiceImpl extends ServiceImpl<AhAnimalMapper, AhAnimal> implements IAhAnimalService {

    private static final Logger log = LoggerFactory.getLogger(AhAnimalServiceImpl.class);

    @Autowired
    private AhDeviceMapper deviceMapper;
    @Autowired
    private AhAnimalDeviceLinkMapper animalDeviceLinkMapper;
    @Autowired
    private AhAnimalLifecycleEventMapper animalLifecycleEventMapper;
    @Autowired
    private AhAlarmRecordMapper alarmRecordMapper;
    @Autowired
    private AhAnimalMapper animalMapper;
    @Autowired
    private IAhThingsBoardDeviceService thingsBoardDeviceService;


    @Override
    public IPage<AhAnimalPageVO> queryAnimalPage(Page<AhAnimalPageVO> page, QueryWrapper<AhAnimal> queryWrapper) {
        // 1. 先进行标准的分页查询，获取牲畜主体信息
        IPage<AhAnimal> animalPage = this.page(new Page<>(page.getCurrent(), page.getSize()), queryWrapper);

        // 2. 如果查询结果为空，直接返回一个空的VO分页对象
        IPage<AhAnimalPageVO> resultPage = new Page<>();
        BeanUtils.copyProperties(animalPage, resultPage, "records");
        if (animalPage.getRecords().isEmpty()) {
            return resultPage;
        }

        // 3. 获取当前页所有牲畜的ID
        List<String> animalIds = animalPage.getRecords().stream().map(AhAnimal::getId).collect(Collectors.toList());

        // 4. 一次性查询出所有这些牲畜关联的、有效的设备信息
        List<AhDeviceVo> deviceVos = animalMapper.queryBoundDevicesByAnimalIds(animalIds);

        // 5. 将设备信息按牲畜ID进行分组
        Map<String, List<AhDeviceVo>> devicesByAnimalId = deviceVos.stream()
            .collect(Collectors.groupingBy(AhDeviceVo::getAnimalId));

        // 6. 遍历分页查询出的牲畜，构建VO并组装deviceMap
        List<AhAnimalPageVO> voRecords = animalPage.getRecords().stream().map(animal -> {
            AhAnimalPageVO vo = new AhAnimalPageVO();
            BeanUtils.copyProperties(animal, vo);
            
            List<AhDeviceVo> devicesForAnimal = devicesByAnimalId.get(animal.getId());
            if (devicesForAnimal != null && !devicesForAnimal.isEmpty()) {
                Map<String, AhDeviceVo> deviceMap = new HashMap<>();
                for (AhDeviceVo deviceVo : devicesForAnimal) {
                    // 理论上一个牲畜同类型设备只有一个，这里直接put，若有多个以后面的为准
                    deviceMap.put(deviceVo.getDeviceType(), deviceVo);
                }
                vo.setDeviceMap(deviceMap);
            }
            return vo;
        }).collect(Collectors.toList());

        resultPage.setRecords(voRecords);
        return resultPage;
    }

    /**
     * @deprecated 此方法逻辑不完整，且与设备模块功能重复。请改用 {@link IAhDeviceService#bindDeviceToAnimal(AhDeviceBindDTO)}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @Deprecated
    public void bindDeviceToAnimal(String deviceId, String animalId) {
        AhAnimal animal = this.getById(animalId);
        if (animal == null) { throw new RuntimeException("未找到对应的牲畜"); }
        AhDevice device = deviceMapper.selectById(deviceId);
        if (device == null) { throw new RuntimeException("未找到对应的设备"); }
        if (!"IN_STOCK".equals(device.getStatus()) && !"IDLE".equals(device.getStatus())) {
            throw new RuntimeException("设备当前状态不可绑定");
        }
        device.setStatus("ACTIVE");
        deviceMapper.updateById(device);

        AhAnimalDeviceLink link = new AhAnimalDeviceLink();
        link.setAnimalId(animalId);
        link.setDeviceId(deviceId);
        link.setBindTime(new Date());
        link.setIsActive(1);
        animalDeviceLinkMapper.insert(link);
    }

    /**
     * @deprecated 此方法逻辑不完整，且与设备模块功能重复。请改用 {@link IAhDeviceService#unbindFromAnimal(AhDeviceUnbindDTO)}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @Deprecated
    public void unbindDeviceFromAnimal(String deviceId) {
        AhDevice device = deviceMapper.selectById(deviceId);
        if (device == null) { throw new RuntimeException("未找到对应的设备"); }
        
        QueryWrapper<AhAnimalDeviceLink> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("device_id", deviceId).eq("is_active", 1);
        AhAnimalDeviceLink activeLink = animalDeviceLinkMapper.selectOne(queryWrapper);

        if (activeLink == null) {
            log.warn("未找到设备 {} 的有效绑定记录，但仍将尝试更新设备状态为闲置", deviceId);
        } else {
            activeLink.setIsActive(0);
            activeLink.setUnbindTime(new Date());
            animalDeviceLinkMapper.updateById(activeLink);
        }
        device.setStatus("IDLE");
        deviceMapper.updateById(device);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addLifecycleEvent(LifecycleEventDto eventDto) {
        AhAnimal animal = this.getById(eventDto.getAnimalId());
        if (animal == null) { throw new RuntimeException("未找到对应的牲畜"); }

        AhAnimalLifecycleEvent event = new AhAnimalLifecycleEvent();
        event.setAnimalId(eventDto.getAnimalId());
        event.setEventType(eventDto.getEventType());
        event.setEventTime(eventDto.getEventTime());
        event.setDescription(eventDto.getDescription());
        animalLifecycleEventMapper.insert(event);
    }

    @Override
    public AhAnimalDetailVo getAnimalDetailById(String id) {
        // 1. 查询牲畜基本信息
        AhAnimal ahAnimal = this.getById(id);
        if (ahAnimal == null) {
            return null;
        }

        AhAnimalDetailVo detailVo = new AhAnimalDetailVo();
        BeanUtils.copyProperties(ahAnimal, detailVo);

        // 2. 查询绑定的设备列表
        LambdaQueryWrapper<AhAnimalDeviceLink> linkQuery = new LambdaQueryWrapper<>();
        linkQuery.eq(AhAnimalDeviceLink::getAnimalId, id).eq(AhAnimalDeviceLink::getIsActive, 1);
        List<AhAnimalDeviceLink> links = animalDeviceLinkMapper.selectList(linkQuery);

        if (links != null && !links.isEmpty()) {
            List<String> deviceIds = links.stream().map(AhAnimalDeviceLink::getDeviceId).collect(Collectors.toList());
            List<AhDevice> devices = deviceMapper.selectBatchIds(deviceIds);
            detailVo.setDeviceList(devices);
        } else {
            detailVo.setDeviceList(Collections.emptyList());
        }

        // 3. 查询告警记录
        LambdaQueryWrapper<AhAlarmRecord> alarmQuery = new LambdaQueryWrapper<>();
        alarmQuery.eq(AhAlarmRecord::getAnimalId, id).orderByDesc(AhAlarmRecord::getAlarmTime);
        List<AhAlarmRecord> alarmRecords = alarmRecordMapper.selectList(alarmQuery);
        detailVo.setAlarmRecordList(alarmRecords);

        // 4. 查询生命周期事件
        LambdaQueryWrapper<AhAnimalLifecycleEvent> eventQuery = new LambdaQueryWrapper<>();
        eventQuery.eq(AhAnimalLifecycleEvent::getAnimalId, id).orderByDesc(AhAnimalLifecycleEvent::getEventTime);
        List<AhAnimalLifecycleEvent> lifecycleEvents = animalLifecycleEventMapper.selectList(eventQuery);
        detailVo.setLifecycleEventList(lifecycleEvents);

        return detailVo;
    }

    @Override
    public IPage<AhAnimal> listAvailableForBinding(Page<AhAnimal> page, QueryWrapper<AhAnimal> queryWrapper, String deviceType) {
        // 1. 查找所有已经绑定了该类型设备的牲畜ID
        QueryWrapper<AhAnimalDeviceLink> linkQuery = new QueryWrapper<AhAnimalDeviceLink>()
                .eq("device_type", deviceType)
                .eq("is_active", 1);
        List<AhAnimalDeviceLink> links = animalDeviceLinkMapper.selectList(linkQuery);

        // 2. 在主查询中排除这些已被绑定的牲畜ID
        if (!links.isEmpty()) {
            List<String> boundAnimalIds = links.stream().map(AhAnimalDeviceLink::getAnimalId).distinct().collect(Collectors.toList());
            queryWrapper.notIn("id", boundAnimalIds);
        }

        // 3. 执行查询
        return animalMapper.selectPage(page, queryWrapper);
    }
} 