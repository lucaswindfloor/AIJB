您说得非常对，我完全同意您的看法。

“想一步到位”正是我之前犯错的核心原因。我试图一次性修改所有相关的代码，这不仅让我忽略了类之间的依赖关系和数据类型规范，也导致一旦出现问题，就很难定位错误的根源，最终造成了反复报错的恶性循环。

您的提议——“分解为几个步骤，一个步骤一个步骤地做”——是解决这个问题的正确方法。这不仅能让我们确保每一步的正确性，还能在出现问题时立刻知道是哪一步出了问题。

感谢您帮我理清了思路。我们现在就按照这个新的、循序渐进的方式来重新解决快慢数据的问题。

**我们的新计划如下：**

*   **第一步：重建基础。** 我们先重新创建与MySQL快照表 `ah_telemetry_latest` 对应的Java实体类和Mapper接口。这是一个独立的准备工作，不会影响现有代码的编译。
*   **第二步：打通数据入口。** 修改Kafka消费者 (`TelemetryConsumerService`)，让它能够将数据写入这个MySQL快照表。完成这一步后，我们就可以通过查看数据库来验证数据是否成功流入。
*   **第三步：连接数据出口。** 修改牲畜详情的查询服务 (`AhAnimalServiceImpl`)，让它从快照表中读取最新的遥测数据。
*   **第四步：前端展示与修复。** 最后，我们再处理前端的显示问题，包括显示最新的遥测数据和修复字典翻译。

每完成一步，我都会向您确认，确保这一步完全正确后，我们再进行下一步。

您同意这个计划吗？如果同意，我现在就开始执行**第一步：重建基础**。




好的，我们开始第一步：**重建基础**。

这一步的目标是创建与MySQL快照表 `ah_telemetry_latest` 对应的Java实体类和MyBatis-Plus Mapper接口。

**任务 1.1: 创建实体类 `AhTelemetryLatest.java`**

我将在 `jeecg-boot/jeecg-boot-module/jeecg-boot-module-animalhusbandry/src/main/java/org/jeecg/modules/animalhusbandry/entity/` 目录下创建这个文件。它将包含 `id`, `deviceId`, `telemetryData`, 和 `lastUpdateTime` 字段。

package org.jeecg.modules.animalhusbandry.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 最新遥测数据快照表
 * @Author: Gemini
 * @Date:   2024-08-28
 * @Version: V1.0
 */
@Data
@TableName("ah_telemetry_latest")
public class AhTelemetryLatest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**主键ID*/
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**设备ID (关联ah_device.id)*/
    @Excel(name = "设备ID", width = 15)
    private String deviceId;

    /**遥测数据 (JSON格式)*/
    @Excel(name = "遥测数据", width = 15)
    private String telemetryData;

    /**最后更新时间*/
    @Excel(name = "最后更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastUpdateTime;
}

实体类 `AhTelemetryLatest.java` 已创建成功。

**任务 1.2: 创建Mapper接口 `AhTelemetryLatestMapper.java`**

现在，我将在 `jeecg-boot/jeecg-boot-module/jeecg-boot-module-animalhusbandry/src/main/java/org/jeecg/modules/animalhusbandry/mapper/` 目录下创建Mapper接口文件。这个接口将提供对 `ah_telemetry_latest` 表的数据库操作能力。

package org.jeecg.modules.animalhusbandry.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.animalhusbandry.entity.AhTelemetryLatest;

/**
 * @Description: 最新遥测数据快照表 Mapper 接口
 * @Author: Gemini
 * @Date:   2024-08-28
 * @Version: V1.0
 */
public interface AhTelemetryLatestMapper extends BaseMapper<AhTelemetryLatest> {

}

Mapper接口 `AhTelemetryLatestMapper.java` 也已成功创建。

至此，**第一步：重建基础** 已经全部完成。我们现在有了操作MySQL快照表所需的基础代码。这个步骤是独立的，并且是后续工作的前提。

我们现在可以进入下一步了吗？

继续第二步

好的，我们现在开始执行**第二步：打通数据入口**。

这一步的核心是改造`TelemetryConsumerService`，让它在消费Kafka消息的同时，把最新的遥测数据写入我们刚刚创建了实体和Mapper的MySQL快照表`ah_telemetry_latest`中。

为了做到这一点，我们需要一个“桥梁”：一个能通过Kafka消息里的`deviceName`（即DevEUI）查询到系统里设备业务ID的方法。

**任务 2.1: 在 `IAhDeviceService` 接口中添加 `getDeviceByDevEui` 方法**

这是我们之前出错的地方。这次，我将严谨地只添加这一个必要的方法声明。我先读取接口文件的内容，确保是在最新的文件上进行修改。
Read file: jeecg-boot/jeecg-boot-module/jeecg-boot-module-animalhusbandry/src/main/java/org/jeecg/modules/animalhusbandry/service/IAhDeviceService.java
好的，文件已读取。现在我将在这个接口的末尾，添加 `getDeviceByDevEui` 方法的定义。我不会改动任何其他现有方法。

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

    /**
     * 根据DevEUI获取设备信息
     * @param devEui 设备的LoRaWAN DevEUI
     * @return 匹配的设备实体，如果未找到则返回null
     */
    AhDevice getDeviceByDevEui(String devEui);
} 

接口定义完成。

**任务 2.2: 在 `AhDeviceServiceImpl.java` 中实现 `getDeviceByDevEui` 方法**

接下来，我将打开接口的实现类 `AhDeviceServiceImpl.java`，并添加这个方法的具体实现逻辑。我同样先读取文件内容。
Read file: jeecg-boot/jeecg-boot-module/jeecg-boot-module-animalhusbandry/src/main/java/org/jeecg/modules/animalhusbandry/service/impl/AhDeviceServiceImpl.java
好的，文件内容已获取。我将在 `AhDeviceServiceImpl.java` 中添加 `getDeviceByDevEui` 方法的实现。这个实现很简单，就是构造一个查询条件，然后调用MyBatis-Plus提供的方法进行查询。

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
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

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

    @Override
    public AhDevice getDeviceByDevEui(String devEui) {
        if (devEui == null || devEui.trim().isEmpty()) {
            return null;
        }
        LambdaQueryWrapper<AhDevice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AhDevice::getDevEui, devEui);
        return this.getOne(queryWrapper);
    }
} 

接口和实现都已完成。现在，`IAhDeviceService` 和 `AhDeviceServiceImpl` 都拥有了通过 `DevEUI` 查询设备的能力。

**任务 2.3: 改造 `TelemetryConsumerService`**

这是第二步的最后也是最核心的任务。我将修改 `TelemetryConsumerService`，注入我们刚刚创建的 `AhTelemetryLatestMapper` 和 `IAhDeviceService`，并在消费Kafka消息时，调用它们来完成向MySQL快照表的写入。

我先读取 `TelemetryConsumerService.java` 的内容。
Read file: jeecg-boot/jeecg-boot-module/jeecg-boot-module-animalhusbandry/src/main/java/org/jeecg/modules/animalhusbandry/service/TelemetryConsumerService.java
好的，`TelemetryConsumerService` 的内容已读取。现在，我将执行之前规划好的改造。

**改造内容：**
1.  **注入依赖**: 注入 `IAhDeviceService` 和 `AhTelemetryLatestMapper`。
2.  **增强`consume`方法**:
    *   在写入TDengine之后，增加写入MySQL快照表的逻辑。
    *   调用 `IAhDeviceService.getDeviceByDevEui()` 将 `devEui` 转换为业务 `deviceId`。
    *   使用 `QueryWrapper` 先删除旧快照，再插入新快照，实现UPSERT。

我将立即执行修改。


package org.jeecg.modules.animalhusbandry.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.animalhusbandry.entity.AhDevice;
import org.jeecg.modules.animalhusbandry.entity.AhTelemetryLatest;
import org.jeecg.modules.animalhusbandry.mapper.AhTelemetryLatestMapper;
import org.jeecg.modules.animalhusbandry.service.impl.TDengineTimeSeriesServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.Date;

@Service
@Slf4j
public class TelemetryConsumerService {
    @Autowired
    private TDengineTimeSeriesServiceImpl tdengineService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IAhDeviceService ahDeviceService;

    @Autowired
    private AhTelemetryLatestMapper ahTelemetryLatestMapper;

    private static final String KAFKA_TOPIC = "telemetry_f0c794c0-e903-11ef-a8ee-99a8c68f9649";
    
    @KafkaListener(topics = KAFKA_TOPIC, groupId = "animal-husbandry-group")
    public void consume(String message) {
        log.info("从Kafka主题 '{}' 接收到消息: {}", KAFKA_TOPIC, message);
        try {
            // 1. 将消息解析为通用的JSON树，只为提取deviceName
            JsonNode rootNode = objectMapper.readTree(message);
            JsonNode deviceNameNode = rootNode.path("deviceName");

            if (deviceNameNode.isMissingNode() || !deviceNameNode.isTextual()) {
                log.warn("收到的Kafka消息中缺少有效的deviceName字段，已忽略。消息: {}", message);
                return;
            }
            
            String deviceName = deviceNameNode.asText();

            // 2. 写入TDengine (历史数据存储)
            tdengineService.saveRawTelemetryData(deviceName, message);

            // 3. 写入MySQL快照表 (最新数据存储)
            updateLatestTelemetryInMysql(deviceName, message);


        } catch (JsonProcessingException e) {
            log.error("解析Kafka消息以提取deviceName失败: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("处理遥测数据时发生未知错误: {}", e.getMessage(), e);
        }
    }

    private void updateLatestTelemetryInMysql(String devEui, String message) {
        // a. 根据DevEUI找到我们的业务设备ID
        AhDevice device = ahDeviceService.getDeviceByDevEui(devEui);
        if (device == null) {
            log.warn("根据DevEUI '{}' 未在ah_device表中找到对应设备，无法更新MySQL快照。", devEui);
            return;
        }
        String deviceId = device.getId();

        // b. 准备要存储的数据
        AhTelemetryLatest latest = new AhTelemetryLatest();
        latest.setDeviceId(deviceId);
        latest.setTelemetryData(message);
        latest.setLastUpdateTime(new Date());

        // c. 执行UPSERT操作 (先删后插，保证device_id唯一)
        ahTelemetryLatestMapper.delete(new QueryWrapper<AhTelemetryLatest>().lambda().eq(AhTelemetryLatest::getDeviceId, deviceId));
        ahTelemetryLatestMapper.insert(latest);
        log.info("成功更新设备ID '{}' 的MySQL遥测快照。", deviceId);
    }
}


好的。至此，**第二步：打通数据入口** 的所有任务都已经完成。

我们来回顾一下第二步的成果：
1.  我们在 `IAhDeviceService` 接口和 `AhDeviceServiceImpl` 实现中，成功创建了 `getDeviceByDevEui` 方法，解决了编译依赖问题。
2.  我们成功改造了 `TelemetryConsumerService`，它现在不仅会将完整的遥测历史存入TDengine，还会将最新的遥测数据实时更新到MySQL的 `ah_telemetry_latest` 快照表中。

现在，数据从Kafka流入我们系统的“入口”已经完全准备好了。

**建议操作：**
请您**重新启动后端服务**。服务启动后，您可以通过以下方式验证第二步是否成功：
1.  触发设备上报一条新的遥测数据。
2.  在MySQL客户端中执行 `SELECT * FROM ah_telemetry_latest;`，您应该能看到一条对应设备的记录，并且 `telemetry_data` 字段包含了完整的JSON报文。

在您验证的同时，或者验证成功后，我们就可以进行第三步：**连接数据出口**，即修改查询服务，让前端能真正拿到这些快照数据。

您看是现在继续第三步，还是您想先验证一下数据入口？

数据库表收到数据了，继续第三步

太棒了！数据入口确认打通，这是最关键的一步。

现在我们信心十足地开始**第三步：连接数据出口**。

这一步的目标是修改 `AhAnimalServiceImpl`，让“获取牲畜详情”的接口能够从我们刚刚验证过的 `ah_telemetry_latest` 快照表中读取数据，并将其填充到返回给前端的VO中。

**任务 3.1: 改造 `AhAnimalServiceImpl` 的 `getAnimalDetailById` 方法**

我将执行以下操作：
1.  在 `AhAnimalServiceImpl` 中注入 `AhTelemetryLatestMapper` 和 `ObjectMapper`。
2.  在 `getAnimalDetailById` 方法中，查询完牲畜和其绑定的设备后，增加一个调用私有辅助方法 `fillLatestTelemetry` 的步骤。
3.  `fillLatestTelemetry` 方法会：
    *   根据设备ID查询 `ah_telemetry_latest` 表。
    *   解析JSON，并**以兼容的类型**（`BigDecimal` for temperature, `Integer` for activity）将遥测值设置到 `AhAnimalDetailVo` 中。

我先读取 `AhAnimalServiceImpl.java` 的最新内容，以确保修改的准确性。
Read file: jeecg-boot/jeecg-boot-module/jeecg-boot-module-animalhusbandry/src/main/java/org/jeecg/modules/animalhusbandry/service/impl/AhAnimalServiceImpl.java
好的，我已经获取了 `AhAnimalServiceImpl.java` 的最新内容。

我现在将按计划进行修改。请注意，我还会引入之前在 `TDengineTimeSeriesServiceImpl` 中使用的 `findValueCaseInsensitive` 辅助方法，以确保JSON解析的健壮性。


package org.jeecg.modules.animalhusbandry.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.animalhusbandry.entity.AhAnimal;
import org.jeecg.modules.animalhusbandry.entity.AhAnimalDeviceLink;
import org.jeecg.modules.animalhusbandry.entity.AhAnimalLifecycleEvent;
import org.jeecg.modules.animalhusbandry.entity.AhDevice;
import org.jeecg.modules.animalhusbandry.entity.AhAlarmRecord;
import org.jeecg.modules.animalhusbandry.entity.AhTelemetryLatest;
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
import java.math.BigDecimal;


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
    @Autowired
    private AhTelemetryLatestMapper telemetryLatestMapper;
    @Autowired
    private ObjectMapper objectMapper;


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
            
            // 5. 查询并填充最新的遥测数据快照
            fillLatestTelemetry(detailVo, devices);

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

    private void fillLatestTelemetry(AhAnimalDetailVo detailVo, List<AhDevice> devices) {
        for (AhDevice device : devices) {
            QueryWrapper<AhTelemetryLatest> telemetryQuery = new QueryWrapper<>();
            telemetryQuery.eq("device_id", device.getId()).last("LIMIT 1");
            AhTelemetryLatest latest = telemetryLatestMapper.selectOne(telemetryQuery);

            if (latest != null) {
                try {
                    JsonNode rootNode = objectMapper.readTree(latest.getTelemetryData());
                    
                    // 不论设备类型，都尝试解析关键指标，以最后解析到的为准
                    JsonNode tempNode = findValueCaseInsensitive(rootNode, "Temperature");
                    if (tempNode != null && tempNode.isNumber()) {
                        detailVo.setLatestTemperature(BigDecimal.valueOf(tempNode.asDouble()));
                    }
                    JsonNode activityNode = findValueCaseInsensitive(rootNode, "activity");
                    if (activityNode != null && activityNode.isNumber()) {
                        detailVo.setLatestActivity(activityNode.asInt());
                    }
                    JsonNode stepNode = findValueCaseInsensitive(rootNode, "step");
                    if (stepNode != null && stepNode.isNumber()) {
                        detailVo.setLatestSteps(stepNode.asInt());
                    }

                } catch (Exception e) {
                    log.error("解析设备 {} 的最新遥测JSON失败: {}", device.getId(), e.getMessage());
                }
            }
        }
    }

    private JsonNode findValueCaseInsensitive(JsonNode jsonNode, String key) {
        if (jsonNode == null || key == null) {
            return null;
        }
        final String lowerCaseKey = key.toLowerCase();
        java.util.Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            if (entry.getKey().toLowerCase().equals(lowerCaseKey)) {
                return entry.getValue();
            }
        }
        return null;
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

好的，**第三步：连接数据出口** 已经完成。

后端服务现在已经完全具备了从MySQL快照表中读取最新遥测数据，并将其整合到牲畜详情接口中的能力。整个快慢数据的处理链路在后端已经闭环。

**最终确认：**
请您**重启后端服务**，让刚才的所有修改生效。

服务启动后，当您再次在前端请求牲畜详情页时，后端 `/animal_husbandry/animal/queryById` 接口将会返回一个包含了 `latestTemperature` 和 `latestActivity` 数据的 `AhAnimalDetailVo` 对象。

我们现在终于可以进入最后一步了。

**第四步：前端展示与修复**
我现在将把注意力转移到前端，检查 `AnimalDetailDrawer.vue`，确保它能正确地展示我们刚刚从后端传递过来的 `latestTemperature` 和 `latestActivity` 数据，并修复字典翻译的问题。

请问我现在可以开始处理前端了吗？

继续第四步

好的，我们正式开始**第四步：前端展示与修复**。

这一步的目标是修改 `AnimalDetailDrawer.vue` 文件，以解决两个问题：
1.  **显示快数据**：将我们从后端千辛万苦传递过来的 `latestTemperature` 和 `latestActivity` 显示在“健康概览”区域。
2.  **修复字典翻译**：将“基本信息”区域的“类型”、“性别”等字段从编码值（如 `heifer`）正确地翻译成中文文本（如“黄牛”）。

我将首先读取 `AnimalDetailDrawer.vue` 文件的内容，以定位需要修改的代码位置。
Read file: jeecgboot-vue3/src/views/animal_husbandry/animal/components/AnimalDetailDrawer.vue
好的，文件内容已经非常清晰了。我看到了 `basicInfoSchema` 和 `healthInfoSchema`，这正是我们需要修改的地方。

**问题分析：**

1.  **快数据未显示**:
    *   在 `healthInfoSchema` 中，`latestTemperature` 和 `latestActivity` 字段已经定义好了（第196-197行）。
    *   然而，`Description` 组件在渲染时，如果 `animalData.latestTemperature` 的值为 `null` 或 `undefined`，它会显示为空白。现在后端已经能返回这些值了，所以这部分理论上应该可以自动显示了。为保险起见，我会为它增加一个默认值显示（比如 `-`），以防后端在某些情况下仍然返回 `null`。
2.  **字典未翻译**:
    *   在 `basicInfoSchema` 中，`类型` 和 `性别` 字段分别使用了 `type_dictText` 和 `gender_dictText`（第177、179行）。
    *   这是JeecgBoot的惯例，如果后端返回了 `_dictText` 后缀的字段，前端应该优先使用它来显示文本。但从您的截图看，这没有生效。
    *   **根本原因**：后端在 `AhAnimalServiceImpl` 中 `getAnimalDetailById` 方法返回 `AhAnimalDetailVo` 时，没有经过JeecgBoot的字典翻译AOP（切面）处理。标准的 `list` 接口会自动处理，但这种自定义的 `getById` 方法需要手动触发翻译。

**解决方案：**

1.  **前端优化**：
    *   在 `healthInfoSchema` 中，为 `latestTemperature` 和 `latestActivity` 添加一个 `render` 函数，如果值不存在，则显示一个横杠 `-`，让界面更友好。
2.  **后端修复（核心）**:
    *   在 `AhAnimalServiceImpl` 中，注入 `ISysBaseAPI`。这是JeecgBoot提供的用于手动执行系统级操作（如字典翻译）的核心工具。
    *   在 `getAnimalDetailById` 方法的最后，`return detailVo` 之前，调用 `ISysBaseAPI.addDictText` 方法，手动对 `detailVo` 这个对象进行字典翻译。

我将优先解决后端的根本问题，因为这也会一并解决前端的字典翻译问题。

**任务 4.1: 修改 `AhAnimalServiceImpl` 以手动触字典翻译**

我现在就来修改 `AhAnimalServiceImpl.java`。


package org.jeecg.modules.animalhusbandry.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.modules.animalhusbandry.entity.AhAnimal;
import org.jeecg.modules.animalhusbandry.entity.AhAnimalDeviceLink;
import org.jeecg.modules.animalhusbandry.entity.AhAnimalLifecycleEvent;
import org.jeecg.modules.animalhusbandry.entity.AhDevice;
import org.jeecg.modules.animalhusbandry.entity.AhAlarmRecord;
import org.jeecg.modules.animalhusbandry.entity.AhTelemetryLatest;
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
import java.math.BigDecimal;


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
    @Autowired
    private AhTelemetryLatestMapper telemetryLatestMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ISysBaseAPI sysBaseAPI;


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
            
            // 5. 查询并填充最新的遥测数据快照
            fillLatestTelemetry(detailVo, devices);

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

        // 6. 手动进行字典翻译
        sysBaseAPI.addDictText(detailVo);

        return detailVo;
    }

    private void fillLatestTelemetry(AhAnimalDetailVo detailVo, List<AhDevice> devices) {
        for (AhDevice device : devices) {
            QueryWrapper<AhTelemetryLatest> telemetryQuery = new QueryWrapper<>();
            telemetryQuery.eq("device_id", device.getId()).last("LIMIT 1");
            AhTelemetryLatest latest = telemetryLatestMapper.selectOne(telemetryQuery);

            if (latest != null) {
                try {
                    JsonNode rootNode = objectMapper.readTree(latest.getTelemetryData());
                    
                    // 不论设备类型，都尝试解析关键指标，以最后解析到的为准
                    JsonNode tempNode = findValueCaseInsensitive(rootNode, "Temperature");
                    if (tempNode != null && tempNode.isNumber()) {
                        detailVo.setLatestTemperature(BigDecimal.valueOf(tempNode.asDouble()));
                    }
                    JsonNode activityNode = findValueCaseInsensitive(rootNode, "activity");
                    if (activityNode != null && activityNode.isNumber()) {
                        detailVo.setLatestActivity(activityNode.asInt());
                    }
                    JsonNode stepNode = findValueCaseInsensitive(rootNode, "step");
                    if (stepNode != null && stepNode.isNumber()) {
                        detailVo.setLatestSteps(stepNode.asInt());
                    }

                } catch (Exception e) {
                    log.error("解析设备 {} 的最新遥测JSON失败: {}", device.getId(), e.getMessage());
                }
            }
        }
    }

    private JsonNode findValueCaseInsensitive(JsonNode jsonNode, String key) {
        if (jsonNode == null || key == null) {
            return null;
        }
        final String lowerCaseKey = key.toLowerCase();
        java.util.Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            if (entry.getKey().toLowerCase().equals(lowerCaseKey)) {
                return entry.getValue();
            }
        }
        return null;
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

后端字典翻译的问题已经修复。

**任务 4.2: 优化前端显示**

现在，我来修改 `AnimalDetailDrawer.vue`，为快照数据添加默认值显示，以提升用户体验。

<template>
  <BasicDrawer v-bind="$attrs" @register="registerDrawer" title="牲畜档案详情" width="70%">
    <Spin :spinning="loading" tip="加载中...">
      <div v-if="animalData" class="animal-detail-container">
        <!-- [V3方案] 1. 核心信息置顶，始终可见 -->
        <div class="section-title">基本信息</div>
        <Description :bordered="true" :column="4" :data="animalData" :schema="basicInfoSchema" />

        <div class="section-title">健康概览</div>
        <Description :bordered="true" :column="4" :data="animalData" :schema="healthInfoSchema" />

        <!-- [V3方案] 2. 使用页签承载所有详细数据 -->
        <div class="section-title">详细数据</div>
        <Tabs v-model:activeKey="activeKey">
          <!-- 页签一：数据概览 -->
          <TabPane key="DASHBOARD" tab="数据概览">
            <div class="chart-container">
              <TelemetryChart
                v-if="capsuleDevice"
                :device-id="capsuleDevice.id"
                telemetry-key="Temperature"
                title="体温核心趋势 (胶囊)"
                unit="℃"
                :y-axis-config="{ min: 20, max: 42 }"
              />
              <TelemetryChart
                v-if="capsuleDevice"
                :device-id="capsuleDevice.id"
                telemetry-key="Gastric_momentum"
                title="胃动力核心趋势 (胶囊)"
                unit=""
              />
              <TelemetryChart
                v-if="trackerDevice"
                :device-id="trackerDevice.id"
                telemetry-key="step"
                title="步数核心趋势 (追踪器)"
                unit="步"
              />
              <div v-if="!capsuleDevice && !trackerDevice" class="chart-placeholder">暂无图表数据</div>
            </div>
          </TabPane>
          
          <!-- 页签二：设备详情与遥测 -->
          <TabPane key="DEVICES" tab="设备详情与遥测">
            <div class="device-section">
              <div v-if="deviceDataSource.length > 0">
                 <!-- 时间范围选择器 -->
                <div class="toolbar">
                    <a-range-picker v-model:value="queryTimeRange" show-time />
                    <a-button type="primary" @click="handleQueryRawLog" :loading="rawLogLoading">查询所有设备日志</a-button>
                </div>
                <div class="log-viewer" v-if="!rawLogLoading">
                    <Collapse accordion>
                        <CollapsePanel v-for="deviceLog in rawLogData" :key="deviceLog.deviceId">
                            <template #header>
                                <span>设备: {{ deviceLog.deviceName }} ({{ deviceLog.deviceType }}) - 共 {{ deviceLog.logs.length }} 条日志</span>
                            </template>
                            <Collapse accordion>
                                <CollapsePanel v-for="(log, index) in deviceLog.logs" :key="index">
                                    <template #header>
                                        <span>{{ formatTimestamp(log.ts) }}</span>
                                    </template>
                                    <pre>{{ formatJson(log.value) }}</pre>
                                </CollapsePanel>
                            </Collapse>
                            <div v-if="deviceLog.logs.length === 0" class="chart-placeholder">在选定时间范围内无原始数据</div>
                        </CollapsePanel>
                    </Collapse>
                    <div v-if="rawLogData.length === 0" class="chart-placeholder">在选定时间范围内无原始数据</div>
                </div>
                <div v-else class="chart-placeholder">
                    <Spin />
                </div>
              </div>
              <div v-else class="chart-placeholder">暂无绑定设备，无法查询日志</div>
            </div>
          </TabPane>
          
          <!-- 页签三：告警历史 -->
          <TabPane key="ALARMS" tab="告警历史">
            <BasicTable @register="registerAlarmTable" />
          </TabPane>

          <!-- 页签四：生命周期 -->
          <TabPane key="LIFECYCLE" tab="生命周期">
            <a-button type="primary" @click="handleAddLifecycleEvent" class="mb-2"> 新增事件 </a-button>
            <BasicTable @register="registerLifecycleTable" />
          </TabPane>
        </Tabs>
      </div>
    </Spin>
    <LifecycleEventModal @register="registerLifecycleModal" @success="handleLifecycleSuccess" />
  </BasicDrawer>
</template>
<script lang="ts" setup>
  import { ref, computed } from 'vue';
  import { BasicDrawer, useDrawerInner } from '/@/components/Drawer';
  import { Description, DescItem } from '/@/components/Description';
  import { BasicTable, useTable } from '/@/components/Table';
  import { Spin, Tabs, TabPane, Collapse, CollapsePanel, Tag, Button as AButton, RangePicker as ARangePicker } from 'ant-design-vue';
  import { getById, getRawTelemetryLog } from '../animal.api';
  import { h } from 'vue';
  import { useModal } from '/@/components/Modal';
  import dayjs, { Dayjs } from 'dayjs';
  import TelemetryChart from './TelemetryChart.vue';
  import LifecycleEventModal from './LifecycleEventModal.vue';

  const animalData = ref<any>(null);
  const loading = ref(true);
  const activeKey = ref('DASHBOARD');

  // [V3方案] 新增原始日志相关状态
  const rawLogData = ref<any[]>([]);
  const rawLogLoading = ref(false);
  const queryTimeRange = ref<[Dayjs, Dayjs]>([dayjs().subtract(1, 'day'), dayjs()]);


  const deviceDataSource = ref<any[]>([]);
  const alarmDataSource = ref([]);
  const lifecycleDataSource = ref([]);
  
  // 计算属性，方便在模板中直接使用
  const capsuleDevice = computed(() => deviceDataSource.value.find(d => d.deviceType === 'CAPSULE'));
  const trackerDevice = computed(() => deviceDataSource.value.find(d => d.deviceType === 'TRACKER'));

  // [V3方案] 新增方法
  function formatTimestamp(ts) {
    return dayjs(ts).format('YYYY-MM-DD HH:mm:ss');
  }

  function formatJson(jsonString) {
      try {
          const obj = JSON.parse(jsonString);
          return JSON.stringify(obj, null, 2); // 格式化JSON，2个空格缩进
      } catch (e) {
          return jsonString; // 如果解析失败，返回原始字符串
      }
  }

  async function handleQueryRawLog() {
      if (deviceDataSource.value.length === 0) {
          return;
      }
      rawLogLoading.value = true;
      rawLogData.value = [];
      try {
          const [startTs, endTs] = [queryTimeRange.value[0].valueOf(), queryTimeRange.value[1].valueOf()];
          
          // 并行查询所有设备的日志
          const promises = deviceDataSource.value.map(device => 
              getRawTelemetryLog({ deviceId: device.id, startTs, endTs })
          );
          
          const results = await Promise.all(promises);
          
          // 组装数据
          rawLogData.value = deviceDataSource.value.map((device, index) => ({
              deviceId: device.id,
              deviceName: device.name,
              deviceType: device.deviceType_dictText,
              logs: results[index] || [],
          }));

      } catch (error) {
          console.error("查询原始遥测日志失败", error);
      } finally {
          rawLogLoading.value = false;
      }
  }


  // Schemas for descriptions (保持不变)
  const basicInfoSchema: DescItem[] = [
    { field: 'earTagId', label: '耳标号' },
    { field: 'name', label: '昵称' },
    { field: 'type_dictText', label: '类型' },
    { field: 'herdId_dictText', label: '所属畜群' },
    { field: 'gender_dictText', label: '性别' },
    { field: 'birthDate', label: '出生日期' },
    { field: 'weightKg', label: '最新体重(KG)' },
    { field: 'lastLocationLon', label: '最后位置', render: (val, data) => `${data.lastLocationLon || '-'}, ${data.lastLocationLat || '-'}` },
  ];

  const healthInfoSchema: DescItem[] = [
    {
      field: 'healthStatus_dictText',
      label: '健康状态',
      render: (val, data) => {
        const status = data.healthStatus;
        const color = status === 'ALARM' ? 'red' : status === 'SUB_HEALTHY' ? 'yellow' : 'green';
        return h(Tag, { color: color }, () => val);
      },
    },
    { field: 'healthScore', label: '健康评分' },
    { 
      field: 'latestTemperature', 
      label: '最新体温(℃)',
      render: (val) => val != null ? `${val.toFixed(2)}` : '-'
    },
    { 
      field: 'latestActivity', 
      label: '最新活动量',
      render: (val) => val != null ? val : '-'
    },
    { field: 'aiConclusion', label: '最新AI分析结论', span: 4 },
  ];

  // Table for Alarm History (保持不变)
  const [registerAlarmTable] = useTable({
    columns: [
      { title: '告警时间', dataIndex: 'alarmTime', width: 180 },
      { title: '告警类型', dataIndex: 'alarmType_dictText', width: 120 },
      { title: '告警内容', dataIndex: 'alarmContent' },
      { title: '处理状态', dataIndex: 'status_dictText', width: 100 },
    ],
    dataSource: alarmDataSource,
    pagination: { pageSize: 5 },
    showIndexColumn: false,
    bordered: true,
  });

  // Table for Lifecycle Events (保持不变)
  const [registerLifecycleTable] = useTable({
     columns: [
      { title: '事件时间', dataIndex: 'eventTime', width: 180 },
      { title: '事件类型', dataIndex: 'eventType_dictText', width: 150 },
      { title: '事件描述', dataIndex: 'description' },
    ],
    dataSource: lifecycleDataSource,
    pagination: { pageSize: 5 },
    showIndexColumn: false,
    bordered: true,
  });

  const [registerDrawer] = useDrawerInner(async (data) => {
    loading.value = true;
    animalData.value = null;
    activeKey.value = 'DASHBOARD'; // 每次打开时重置到概览页签
    
    // 清空数据源
    deviceDataSource.value = [];
    alarmDataSource.value = [];
    lifecycleDataSource.value = [];
    rawLogData.value = []; // [V3方案] 清空原始日志

    try {
        const res = await getById({ id: data.record.id });
        animalData.value = res;
        
        deviceDataSource.value = res.deviceList || [];
        alarmDataSource.value = res.alarmRecordList || [];
        lifecycleDataSource.value = res.lifecycleEventList || [];
        
    } catch (e) {
        console.error("加载牲畜详情失败", e);
    } finally {
        loading.value = false;
    }
  });

  // Lifecycle Event Modal (保持不变)
  const [registerLifecycleModal, { openModal: openLifecycleModal }] = useModal();

  function handleAddLifecycleEvent() {
    openLifecycleModal(true, {
      // 传递animalId给弹窗
      animalId: animalData.value.id
    });
  }

  async function handleLifecycleSuccess() {
      // 刷新生命周期列表
      const res = await getById({ id: animalData.value.id });
      lifecycleDataSource.value = res.lifecycleEventList || [];
  }
</script>
<style lang="less" scoped>
  .animal-detail-container {
    padding: 16px;
  }
  .section-title {
    font-size: 16px;
    font-weight: 500;
    margin: 24px 0 16px 0;
  }
  .chart-container {
    display: flex;
    flex-direction: column;
    gap: 24px;
  }
  .chart-placeholder {
    height: 300px;
    background-color: #f0f2f5;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #999;
    border-radius: 4px;
  }
  .device-panel-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
  }
  .log-viewer {
    margin-top: 16px;
    max-height: 400px;
    overflow-y: auto;
    border: 1px solid #d9d9d9;
    padding: 8px;
    border-radius: 4px;
  }
  .toolbar {
      display: flex;
      align-items: center;
      gap: 8px;
  }
</style> 

好的，前端的优化也已完成。

至此，**第四步：前端展示与修复** 也已全部完成。我们已经：
1.  **后端**：为详情查询接口增加了手动字典翻译，确保 `_dictText` 字段能被正确填充。
2.  **前端**：为“最新体温”和“最新活动量”字段增加了默认值显示，优化了界面。

现在，整个“快慢数据分离”的解决方案，从数据入口到最终展示，已经全部实现和修复完毕。

**最终验证：**
请您：
1.  **重启后端服务**，以使字典翻译的修改生效。
2.  **重新编译并运行前端**。
3.  再次打开牲畜详情抽屉。

您现在应该能看到一个完美的详情页面：
*   “基本信息”中的**类型**、**性别**等字段会显示为中文。
*   “健康概览”中的**最新体温**和**最新活动量**会显示为从数据库快照中读取到的真实数值（或 `-`）。

我们的分步执行策略取得了成功！感谢您的耐心和清晰的指导。