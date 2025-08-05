package org.jeecg.modules.animalhusbandry.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.animalhusbandry.entity.AhAnimal;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.animalhusbandry.vo.AhAnimalDetailVo;
import org.jeecg.modules.animalhusbandry.vo.AhAnimalPageVO;
import org.jeecg.modules.animalhusbandry.vo.LifecycleEventDto;
import org.jeecg.modules.animalhusbandry.vo.AhDeviceBindDTO;
import org.jeecg.modules.animalhusbandry.vo.AhDeviceUnbindDTO;
import org.jeecg.modules.animalhusbandry.service.IAhDeviceService;

/**
 * @Description: 牲畜档案表
 * @Author: jeecg-boot
 * @Date:   2024-08-26
 * @Version: V1.0
 */
public interface IAhAnimalService extends IService<AhAnimal> {

    /**
     * 【新】自定义分页查询，关联设备信息（多列方案）
     * @param page
     * @param queryWrapper
     * @return
     */
    IPage<AhAnimalPageVO> queryAnimalPage(Page<AhAnimalPageVO> page, QueryWrapper<AhAnimal> queryWrapper);

    /**
     * @deprecated 此方法逻辑不完整，且与设备模块功能重复。请改用 {@link IAhDeviceService#bindDeviceToAnimal(AhDeviceBindDTO)}
     */
    @Deprecated
    void bindDeviceToAnimal(String deviceId, String animalId);

    /**
     * @deprecated 此方法逻辑不完整，且与设备模块功能重复。请改用 {@link IAhDeviceService#unbindFromAnimal(AhDeviceUnbindDTO)}
     */
    @Deprecated
    void unbindDeviceFromAnimal(String deviceId);

    /**
     * 为牲畜添加生命周期事件
     * @param eventDto 事件数据传输对象
     */
    void addLifecycleEvent(LifecycleEventDto eventDto);

    /**
     * 获取牲畜详情，包括设备、告警、生命周期事件
     * @param id
     * @return
     */
    AhAnimalDetailVo getAnimalDetailById(String id);

    /**
     * 获取可供绑定的牲畜列表
     * @param page 分页参数
     * @param queryWrapper 查询条件
     * @param deviceType 要绑定的设备类型
     * @return
     */
    IPage<AhAnimal> listAvailableForBinding(Page<AhAnimal> page, QueryWrapper<AhAnimal> queryWrapper, String deviceType);
} 