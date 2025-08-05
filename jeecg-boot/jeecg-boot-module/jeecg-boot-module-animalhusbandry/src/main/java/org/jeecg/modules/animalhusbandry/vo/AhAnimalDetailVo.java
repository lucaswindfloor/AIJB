package org.jeecg.modules.animalhusbandry.vo;

import lombok.Data;
import org.jeecg.modules.animalhusbandry.entity.AhAlarmRecord;
import org.jeecg.modules.animalhusbandry.entity.AhAnimal;
import org.jeecg.modules.animalhusbandry.entity.AhAnimalLifecycleEvent;
import org.jeecg.modules.animalhusbandry.entity.AhDevice;

import java.util.List;

/**
 * @Description: 牲畜档案详情VO
 * @Author: Gemini
 * @Date: 2024-08-27
 * @Version: V1.0
 */
@Data
public class AhAnimalDetailVo extends AhAnimal {

    /**
     * 绑定的设备列表
     */
    private List<AhDevice> deviceList;

    /**
     * 历史告警记录
     */
    private List<AhAlarmRecord> alarmRecordList;

    /**
     * 生命周期事件记录
     */
    private List<AhAnimalLifecycleEvent> lifecycleEventList;

} 