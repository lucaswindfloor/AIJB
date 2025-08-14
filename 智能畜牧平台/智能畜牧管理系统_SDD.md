# 智能畜牧管理系统 - 系统设计文档 (SDD)

**版本：** 2.2
**日期：** 2024-08-23

---

## 1. 系统概述

本系统旨在构建一个基于 JeecgBoot 平台的智能畜牧健康管理应用。系统将整合物联网(IoT)、地理信息系统(GIS)、大数据和人工智能(AI)技术，实现对牧场牛群的实时监控、健康分析、疾病预警和可视化管理。系统将与第三方物联网平台（如 ThingsBoard）对接，接收并处理来自**瘤胃胶囊**和**动物追踪器**的遥测数据，为牧场提供从"被动治疗"到"主动预防"的数字化转型方案。本文档将详细阐述系统的技术架构、数据库设计、模块设计和数据流，以指导后续的开发工作。

## 2. 技术架构

系统采用前后端分离的微服务架构，以 JeecgBoot 为核心业务中台，整合物联网平台与AI分析服务。

```mermaid
graph TD
    subgraph 基础设施层 (IaaS)
        A[云服务器 / 本地数据中心]
    end

    subgraph 数据与物联层 (Data & IoT)
        B1[瘤胃胶囊<br>体温, 活动量] --> C{LoRaWAN 网关};
        B2[动物追踪器<br>GPS位置, 步数] --> C;
        C --> D[物联网平台<br>ThingsBoard];
        D -- "规则引擎<br>推送到Kafka Topic" --> K[Apache Kafka];
    end

    subgraph 核心中台层 (PaaS - JeecgBoot)
        subgraph 核心数据库
            E[MySQL 8<br>业务核心数据]
            F[TDengine<br>时序遥测数据]
        end
        K -- "消费" --> H(jeecg-module-animal-husbandry);
        H -- "写入" --> F;
        H -- "读写" --> E;
        I(jeecg-ai-service<br>AI分析服务模块) -- "读" --> E & F;
        I -- "写分析结果" --> E;
    end
    
    subgraph 展现与交互层 (SaaS)
        J[jeecgboot-vue3<br>Web管理后台] --> H;
        J --> I;
        K[移动App] --> H;
        K --> I;
    end

    style G fill:#f9f,stroke:#333,stroke-width:2px
    style H fill:#f9f,stroke:#333,stroke-width:2px
    style I fill:#f9f,stroke:#333,stroke-width:2px
```
*   **数据与物联层:**
    *   **物联网平台 (ThingsBoard):** 负责 LoRaWAN 设备的接入、解码和管理。通过其规则引擎，将解析后的遥测数据实时地以 Webhook 方式 POST 到本系统的物联网网关模块。
    *   **数据存储分离策略:**
        *   **MySQL:** 由 JeecgBoot 管理，存储核心业务数据，如牲畜档案、设备台账、用户、告警记录、AI分析结果。此数据库为高频业务查询优化。
        *   **TDengine:** 独立部署的时序数据库，用于存储物联网设备上报的**全部历史遥测数据的原始JSON**。此数据库为AI分析和历史数据图表查询提供高性能支持。

*   **核心中台层 (基于 JeecgBoot):**
    *   `jeecg-module-animal-husbandry` (核心业务模块): 继承自现有模块，负责牲畜档案、设备管理、设备与牲畜的绑定、告警管理等核心功能。**该模块内包含了Kafka消费者服务和TDengine时序数据服务**，负责：
        *   **`TelemetryConsumerService`**: 消费来自Kafka的遥测数据，并调用TDengine服务存入原始JSON。
        *   **`TDengineTimeSeriesServiceImpl`**: 封装对TDengine的所有操作，包括数据写入，以及为前端提供解析后的图表数据和原始日志数据。
    *   `jeecg-ai-service` (新建-AI分析服务模块): 内置健康评估、发情监测等AI模型。通过定时任务或事件触发，从数据库中拉取牲畜的生理指标时序数据进行分析，并将分析结论（如健康评分、发情概率、疾病风险）更新回该牲畜的档案中。

*   **展现与交互层:**
    *   **`jeecgboot-vue3`:** 系统的前端项目，包含牧场驾驶舱、牲畜档案管理、AI预警中心等核心功能页面。

## 3. 数据库设计 (MySQL)

数据库设计严格遵循 `parking_system_tables.sql` 的设计规范，表名前缀统一为 `ah_`。

### 3.1 核心实体表

*   **`ah_herd` (畜群管理表):** - **新增**
    ```sql
    CREATE TABLE `ah_herd` (
      `id` varchar(36) NOT NULL COMMENT '主键ID',
      `name` varchar(100) NOT NULL COMMENT '畜群名称/编号',
      `farm_id` varchar(36) DEFAULT NULL COMMENT '所属牧场ID (用于多牧场扩展)',
      `manager_id` varchar(36) DEFAULT NULL COMMENT '负责人用户ID',
      `description` text COMMENT '描述',
      `create_by` varchar(50) DEFAULT NULL,
      `create_time` datetime(3) DEFAULT NULL,
      `update_by` varchar(50) DEFAULT NULL,
      `update_time` datetime(3) DEFAULT NULL,
      PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='畜群管理表';
    ```

*   **`ah_animal` (牲畜档案表):** 核心档案，实现"一畜一档"。
    ```sql
    CREATE TABLE `ah_animal` (
      `id` varchar(36) NOT NULL COMMENT '主键ID',
      `ear_tag_id` varchar(50) DEFAULT NULL COMMENT '耳标号 (唯一)',
      `name` varchar(100) DEFAULT NULL COMMENT '牲畜昵称',
      `type` varchar(36) DEFAULT NULL COMMENT '牲畜类型 (字典: animal_type, 如黄牛、奶牛)',
      `herd_id` varchar(36) DEFAULT NULL COMMENT '所属畜群ID (关联 ah_herd.id)',
      `gender` varchar(10) DEFAULT NULL COMMENT '性别 (字典: sex)',
      `birth_date` date DEFAULT NULL COMMENT '出生日期',
      `weight_kg` decimal(10,2) DEFAULT NULL COMMENT '最新体重(KG)',
      `health_status` varchar(20) DEFAULT 'HEALTHY' COMMENT '健康状态 (字典: health_status, 如HEALTHY, SUB_HEALTHY, ALARM)',
      `health_score` int(11) DEFAULT '100' COMMENT '健康评分 (0-100)',
      `ai_conclusion` varchar(255) DEFAULT '一切正常' COMMENT '最新AI分析结论',
      `last_location_lon` decimal(10,7) DEFAULT NULL COMMENT '最后更新经度',
      `last_location_lat` decimal(10,7) DEFAULT NULL COMMENT '最后更新纬度',
      `create_by` varchar(50) DEFAULT NULL,
      `create_time` datetime(3) DEFAULT NULL,
      `update_by` varchar(50) DEFAULT NULL,
      `update_time` datetime(3) DEFAULT NULL,
      PRIMARY KEY (`id`),
      UNIQUE KEY `uk_ear_tag_id` (`ear_tag_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='牲畜档案表';
    ```

*   **`ah_device` (设备信息表):** 统一管理瘤胃胶囊和追踪器。
    ```sql
    CREATE TABLE `ah_device` (
      `id` varchar(36) NOT NULL COMMENT '主键ID',
      `name` varchar(100) NOT NULL COMMENT '设备名称',
      `device_type` varchar(20) NOT NULL COMMENT '设备类型 (字典: device_type, CAPSULE-瘤胃胶囊, TRACKER-追踪器)',
      `dev_eui` varchar(50) DEFAULT NULL COMMENT 'LoRaWAN DevEUI (唯一)',
      `tb_device_id` varchar(50) DEFAULT NULL COMMENT 'ThingsBoard平台设备ID (唯一)',
      `model` varchar(100) DEFAULT NULL COMMENT '设备型号',
      `purchase_date` date DEFAULT NULL COMMENT '采购日期',
      `status` varchar(20) NOT NULL DEFAULT 'IN_STOCK' COMMENT '设备生命周期状态 (字典: device_lifecycle_status)',
      `last_signal_rssi` int(11) DEFAULT NULL COMMENT '最后一次的信号强度RSSI',
      `battery_level` int(11) DEFAULT NULL COMMENT '电量百分比',
      `firmware_version` varchar(50) DEFAULT NULL COMMENT '固件版本',
      `create_by` varchar(50) DEFAULT NULL,
      `create_time` datetime(3) DEFAULT NULL,
      `update_by` varchar(50) DEFAULT NULL,
      `update_time` datetime(3) DEFAULT NULL,
      PRIMARY KEY (`id`),
      UNIQUE KEY `uk_dev_eui` (`dev_eui`),
      UNIQUE KEY `uk_tb_device_id` (`tb_device_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备信息表';
    ```

### 3.2 关联与记录表

*   **`ah_animal_device_link` (牲畜设备关联表):** 建立牲畜与设备的绑定关系。
    ```sql
    CREATE TABLE `ah_animal_device_link` (
      `id` varchar(36) NOT NULL COMMENT '主键ID',
      `animal_id` varchar(36) NOT NULL COMMENT '牲畜ID',
      `device_id` varchar(36) NOT NULL COMMENT '设备ID',
      `bind_time` datetime(3) NOT NULL COMMENT '绑定时间',
      `unbind_time` datetime(3) DEFAULT NULL COMMENT '解绑时间',
      `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否当前有效 (1-是, 0-否)',
      PRIMARY KEY (`id`),
      KEY `idx_animal_id` (`animal_id`),
      KEY `idx_device_id` (`device_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='牲畜设备关联表';
    ```

*   **`ah_alarm_record` (告警记录表):**
    ```sql
    CREATE TABLE `ah_alarm_record` (
      `id` varchar(36) NOT NULL COMMENT '主键ID',
      `animal_id` varchar(36) NOT NULL COMMENT '告警牲畜ID',
      `alarm_type` varchar(50) NOT NULL COMMENT '告警类型 (字典: alarm_type, 如: a_temp_high, a_inactive, a_possible_estrus)',
      `alarm_level` varchar(20) NOT NULL DEFAULT 'WARN' COMMENT '告警级别 (字典: alarm_level, WARN, CRITICAL)',
      `alarm_content` varchar(500) NOT NULL COMMENT '告警内容描述',
      `alarm_time` datetime(3) NOT NULL COMMENT '告警发生时间',
      `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '处理状态 (字典: process_status, PENDING, PROCESSING, RESOLVED, IGNORED)',
      `handler_id` varchar(36) DEFAULT NULL COMMENT '处理人ID',
      `handle_time` datetime(3) DEFAULT NULL COMMENT '处理时间',
      `handle_notes` text COMMENT '处理备注',
      `create_time` datetime(3) DEFAULT NULL,
      PRIMARY KEY (`id`),
      KEY `idx_animal_id_time` (`animal_id`, `alarm_time`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警记录表';
    ```
    
*   **`ah_animal_lifecycle_event` (生命周期事件表):** - **新增**
    ```sql
    CREATE TABLE `ah_animal_lifecycle_event` (
      `id` varchar(36) NOT NULL,
      `animal_id` varchar(36) NOT NULL COMMENT '关联的牲畜ID',
      `event_type` varchar(50) NOT NULL COMMENT '事件类型 (字典: animal_event_type)',
      `event_time` datetime(3) NOT NULL COMMENT '事件发生时间',
      `description` text COMMENT '事件详细描述 (如药品名称、配种公牛编号等)',
      `create_by` varchar(50) DEFAULT NULL,
      `create_time` datetime(3) DEFAULT NULL,
      PRIMARY KEY (`id`),
      KEY `idx_animal_id` (`animal_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='牲畜生命周期事件表';
    ```

*   **`ah_firmware` (固件版本管理表):** - **新增**
    ```sql
    CREATE TABLE `ah_firmware` (
      `id` varchar(36) NOT NULL COMMENT '主键ID',
      `device_type` varchar(20) NOT NULL COMMENT '适用的设备类型',
      `version` varchar(50) NOT NULL COMMENT '固件版本号',
      `file_url` varchar(255) NOT NULL COMMENT '固件文件存储地址',
      `file_size` bigint(20) DEFAULT NULL COMMENT '文件大小(Bytes)',
      `checksum` varchar(100) DEFAULT NULL COMMENT '文件校验和 (MD5/SHA256)',
      `description` text COMMENT '版本更新说明',
      `upload_time` datetime(3) NOT NULL,
      `create_by` varchar(50) DEFAULT NULL,
      PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='固件版本管理表';
    ```

*   **`ah_fota_task` (固件升级任务表):** - **新增**
    ```sql
    CREATE TABLE `ah_fota_task` (
      `id` varchar(36) NOT NULL COMMENT '主键ID',
      `firmware_id` varchar(36) NOT NULL COMMENT '目标固件ID',
      `task_name` varchar(100) DEFAULT NULL COMMENT '任务名称',
      `target_selection_type` varchar(20) NOT NULL COMMENT '升级目标类型 (ALL, BY_DEVICE, BY_HERD)',
      `target_ids_json` json DEFAULT NULL COMMENT '升级目标的ID列表 (JSON数组)',
      `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '任务状态 (PENDING, IN_PROGRESS, COMPLETED, FAILED, CANCELED)',
      `scheduled_time` datetime(3) DEFAULT NULL COMMENT '预定的开始时间 (NULL为立即开始)',
      `completion_time` datetime(3) DEFAULT NULL COMMENT '任务完成时间',
      `create_by` varchar(50) DEFAULT NULL,
      `create_time` datetime(3) DEFAULT NULL,
      PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='固件升级任务表';
    ```

*   **`ah_telemetry_latest` (最新遥测数据快照表):** 用于存储每个设备最新的遥测数据，供前端快速拉取展示。
    ```sql
    CREATE TABLE `ah_telemetry_latest` (
      `id` varchar(36) NOT NULL,
      `device_id` varchar(36) NOT NULL COMMENT '设备ID',
      `telemetry_data` json NOT NULL COMMENT '遥测数据 (JSON格式)',
      `last_update_time` datetime(3) NOT NULL COMMENT '最后更新时间',
      PRIMARY KEY (`id`),
      UNIQUE KEY `uk_device_id` (`device_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='最新遥测数据快照表';
    ```

*   **`ah_alarm_rule` (告警规则配置表):** - **新增**
    ```sql
    CREATE TABLE `ah_alarm_rule` (
      `id` varchar(36) NOT NULL COMMENT '主键ID',
      `name` varchar(100) NOT NULL COMMENT '规则名称 (如: 高温告警)',
      `alarm_type` varchar(50) NOT NULL COMMENT '关联的告警类型 (同 ah_alarm_record.alarm_type)',
      `device_type` varchar(20) NOT NULL COMMENT '适用的设备类型 (CAPSULE, TRACKER)',
      `telemetry_key` varchar(50) NOT NULL COMMENT '监控的遥测数据字段名 (如: Temperature, step)',
      `operator` varchar(10) NOT NULL COMMENT '比较操作符 (>, <, =)',
      `threshold_value` varchar(50) NOT NULL COMMENT '阈值',
      `duration_seconds` int(11) DEFAULT '0' COMMENT '持续时间(秒, 0表示瞬时触发)',
      `is_enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用 (1-是, 0-否)',
      `create_by` varchar(50) DEFAULT NULL,
      `create_time` datetime(3) DEFAULT NULL,
      `update_by` varchar(50) DEFAULT NULL,
      `update_time` datetime(3) DEFAULT NULL,
      PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警规则配置表';
    ```

*   **`ah_daily_stats_report` (每日运营统计报表):** - **新增**
    ```sql
    CREATE TABLE `ah_daily_stats_report` (
      `id` varchar(36) NOT NULL COMMENT '主键ID',
      `report_date` date NOT NULL COMMENT '统计日期',
      `farm_id` varchar(36) DEFAULT NULL COMMENT '牧场ID',
      `herd_id` varchar(36) DEFAULT NULL COMMENT '畜群ID (NULL表示全场统计)',
      `total_animals` int(11) DEFAULT '0' COMMENT '牲畜总数',
      `healthy_count` int(11) DEFAULT '0' COMMENT '健康数量',
      `sub_healthy_count` int(11) DEFAULT '0' COMMENT '亚健康数量',
      `alarm_count` int(11) DEFAULT '0' COMMENT '告警数量',
      `new_alarms_count` int(11) DEFAULT '0' COMMENT '当日新增告警数',
      `avg_temperature` decimal(5,2) DEFAULT NULL COMMENT '平均体温',
      `avg_activity` decimal(10,2) DEFAULT NULL COMMENT '平均活动量',
      `create_time` datetime(3) DEFAULT NULL,
      PRIMARY KEY (`id`),
      UNIQUE KEY `uk_farm_herd_date` (`farm_id`, `herd_id`, `report_date`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日运营统计报表';
    ```

### 3.3 数据字典建议

为了确保系统内部状态的一致性，以下为推荐在 JeecgBoot `系统管理 -> 数据字典` 中预先配置的字典项：

| 字典Code        | 字典项文本     | 字典项Value       | 备注                               |
|-----------------|----------------|-------------------|------------------------------------|
| `animal_type`   | 黄牛           | `heifer`          | 牲畜品种                           |
| `animal_type`   | 奶牛           | `dairy_cow`       |                                    |
| `gender`        | 雄性           | `male`            |                                    |
| `gender`        | 雌性           | `female`          |                                    |
| `health_status` | 健康           | `HEALTHY`         | 对应前端图标颜色：绿               |
| `health_status` | 亚健康         | `SUB_HEALTHY`     | 对应前端图标颜色：黄               |
| `health_status` | 告警           | `ALARM`           | 对应前端图标颜色：红               |
| `device_type`   | 瘤胃胶囊       | `CAPSULE`         |                                    |
| `device_type`   | 动物追踪器     | `TRACKER`         |                                    |
| `device_lifecycle_status` | 库存中         | `IN_STOCK`        | (对应原`device_status`) 设备在仓库，未激活 |
| `device_lifecycle_status` | 在用         | `ACTIVE`          | 设备已绑定牲畜，正常工作 |
| `device_lifecycle_status` | 闲置         | `IDLE`            | 设备解绑后，功能正常，可再次使用 |
| `device_lifecycle_status` | 维保中         | `MAINTENANCE`     | 设备故障或充电中，暂时不可用 |
| `device_lifecycle_status` | 已报废         | `RETIRED`         | 设备已损坏，无法再使用 |
| `alarm_type`    | 高温告警       | `a_temp_high`     | a_前缀代表animal                   |
| `alarm_type`    | 低温告警       | `a_temp_low`      |                                    |
| `alarm_type`    | 活动量过低     | `a_inactive`      |                                    |
| `alarm_type`    | 疑似发情       | `a_possible_estrus`|                                    |
| `alarm_type`    | 越界告警       | `g_cross_border`  | g_前缀代表gis                      |
| `alarm_level`   | 警告           | `WARN`            |                                    |
| `alarm_level`   | 严重           | `CRITICAL`        |                                    |
| `process_status`| 待处理         | `PENDING`         |                                    |
| `process_status`| 处理中         | `PROCESSING`      |                                    |
| `process_status`| 已解决         | `RESOLVED`        |                                    |
| `process_status`| 已忽略         | `IGNORED`         |                                    |
| `animal_event_type` | 出生       | `BIRTH`           | (新增) 牲畜生命周期事件            |
| `animal_event_type` | 免疫接种   | `VACCINATION`     |                                    |
| `animal_event_type` | 治疗       | `TREATMENT`       |                                    |
| `animal_event_type` | 配种       | `BREEDING`        |                                    |
| `animal_event_type` | 分娩       | `CALVING`         |                                    |
| `animal_event_type` | 称重       | `WEIGHING`        |                                    |
| `animal_event_type` | 转群       | `HERD_TRANSFER`   |                                    |

## 4. 模块设计

### 4.1 后端模块

#### 4.1.1 `jeecg-module-animal-husbandry` (畜牧核心业务模块)

*   **Controller:**
    *   `AnimalController`: 负责牲畜档案的增删改查。核心接口`queryById`返回聚合了设备、告警等多维信息的`AnimalVo`；提供`bindDevice`, `unbindDevice`, `addLifecycleEvent`等核心业务操作。
    *   `DeviceController`: 负责**设备台账**管理，实现设备作为一种“资产”的同步入库、信息维护和生命周期状态变更。
    *   `DeviceMonitorController`: **(新增)** 负责**设备监控**，提供获取设备运行时KPI、查询问题设备列表和下发远程指令(RPC)的API。
    *   `DashboardController`: 专为**牧场驾驶舱**提供高度聚合的数据接口，如`/kpi`和`/map-data`，为前端性能优化。
    *   `AlarmRecordController`: 提供告警记录的查询和处理（如批量忽略、标记为已处理）接口。
    *   `FenceController`: **(新增)** 提供电子围栏的标准增删改查(CRUD)接口。
    *   `AlarmRuleController`: **(新增)** 负责告警规则的增删改查，并提供动态查询遥测字段的辅助接口。
    *   `FirmwareController`: **(新增)** 提供固件的上传、查询和FOTA任务的创建与管理API。
    *   `ReportController`: **(新增)** 为前端报表页面提供API，仅查询`ah_daily_stats_report`等预聚合结果表。
*   **Service/ServiceImpl:**
    *   `AnimalService`: 封装牲畜档案的核心业务，包括调用`IThingsBoardService`在设备绑定/解绑时更新ThingsBoard的服务端属性。
    *   `DeviceService`/`DeviceMonitorService`: 分别实现设备台账和设备监控的业务逻辑。
    *   `DashboardService`: 封装驾驶舱的数据聚合逻辑。
    
#### 4.1.2 ~~`jeecg-iot-gateway` (物联网网关模块) - **新建**~~ (已废弃)
> **设计演进**: 经过迭代，我们决定将Kafka数据消费的逻辑直接内聚在核心业务模块 `jeecg-module-animal-husbandry` 中，而不是创建一个独立的网关模块。这简化了项目结构，并使得业务逻辑更加集中。


#### 4.1.3 `jeecg-ai-service` (AI分析服务模块) - **新建**

*   **Service/ServiceImpl:**
    *   `AnalysisService`: 提供 AI 分析的核心方法，拉取时序数据进行健康评分、发情预测等。
    *   `StatisticsService`: **(新增职责)** 提供每日统计服务，负责执行重量级的预聚合计算，并将结果写入`ah_daily_stats_report`表。
*   **定时任务 (XXL-Job):**
    *   配置定时任务调用`AnalysisService`进行健康分析。
    *   **新增**: 配置每日统计任务调用 `StatisticsService` 生成运营报表。

### 4.2 前端设计 (基于 jeecgboot-vue3)

*   **视图 (View):**
    *   `views/animal_husbandry/dashboard/index.vue`: **牧场驾驶舱**，核心地图监控页面。
    *   `views/animal_husbandry/animal/AnimalList.vue`: **牲畜档案管理**，实现"一畜一档"的列表与详情页。
    *   `views/animal_husbandry/fence/FenceList.vue`: **(新增)** 电子围栏管理页面，提供围栏的增删改查界面。
    *   `views/animal_husbandry/device/DeviceList.vue`: **设备台账管理**，管理设备资产信息和生命周期。
    *   `views/animal_husbandry/device/DeviceDashboard.vue`: **设备监控仪表盘**，展示设备运行时状态、遥测历史、发送远程指令。
    *   `views/animal_husbandry/alarm/AlarmCenter.vue`: **AI预警中心**，展示和处理所有告警。
    *   `views/animal_husbandry/rule/AlarmRuleList.vue`: **告警规则配置**，管理告警触发条件。
    *   `views/animal_husbandry/report/DailyStats.vue`: **运营统计报表**，可视化展示统计数据。
    *   `views/animal_husbandry/fota/FirmwareList.vue`: **固件管理与升级**，管理FOTA任务。
*   **组件 (Component):**
    *   `MapMonitor.vue`: 封装地图引擎的组件。
    *   `TelemetryChart.vue`: 封装ECharts的组件，用于展示历史数据曲线。

## 5. 核心数据流设计

### 5.1 实时数据上报与监控流程

1.  **设备 -> ThingsBoard:** 传感器上报数据。
2.  **ThingsBoard -> Kafka:** ThingsBoard规则引擎将处理后的JSON数据推送到指定的Kafka Topic。
3.  **数据消费与入库:** `jeecg-module-animal-husbandry` 中的 `TelemetryConsumerService` 消费Kafka消息，并调用 `TDengineTimeSeriesServiceImpl` 将原始JSON数据存入TDengine。
4.  **前端数据拉取:**
    *   **驾驶舱 (轮询):** `MapMonitor.vue` 组件通过定时器调用 `DashboardController` 的 `/map-data` 接口，拉取牲畜最新状态和位置（数据源自MySQL快照表）。
    *   **详情页 (按需):** `AnimalDetailDrawer.vue` 在需要时调用 `AhAnimalController` 的接口，后者通过 `TDengineTimeSeriesServiceImpl` 从TDengine中查询特定时间范围的历史数据。
5.  **前端渲染:** 组件更新地图或图表。

### 5.2 AI 分析与状态更新流程

1.  **定时触发:** XXL-Job 定时触发 `jeecg-ai-service` 的分析任务。
2.  **数据拉取:** `AnalysisService` 从 **TDengine/InfluxDB** 拉取时序数据。
3.  **模型分析:** AI模型计算生成健康得分、结论。
4.  **结果入库:** `AnalysisService` 将结果更新回 `ah_animal` 表。
5.  **前端展示:** 驾驶舱和详情页直接读取`ah_animal`中由AI更新的字段。

### 5.3 ThingsBoard 集成策略 - (强化)

为确保本平台与 ThingsBoard 的高效、稳定集成，我们采用以下全局策略：

*   **使用服务端属性 (Server-side Attributes):**
    *   当在本平台进行 **绑定/解绑** 操作时，除了更新自身数据库，还必须通过 ThingsBoard 的 API，同步更新设备的服务端属性（如 `animalId`, `animalEarTag`, `platformBindStatus`）。
    *   **价值：** 极大提高在ThingsBoard平台直接排查问题的效率。

*   **使用远程过程调用 (RPC):**
    *   本平台向设备下发指令时（如FOTA升级、请求重启），应调用 ThingsBoard 的双向或单向RPC接口。
    *   **价值：** 利用ThingsBoard成熟的设备通信链路，无需本平台直接处理复杂的物联网协议。
    *   **注意**: 远程指令的下发API应位于 `DeviceMonitorController` 中。

## 6. 开发实施建议

1.  **数据库先行:** 根据本文档中的SQL脚本，在数据库中创建所有 `ah_` 相关表。
2.  **代码生成:** 使用 JeecgBoot 代码生成器，一键生成 `Animal`, `Device`, `AlarmRule`等核心实体的前后端基础代码。
3.  **分步实施:**
    *   **第一步 (基础框架搭建):**
        *   完成后端 `jeecg-module-animal-husbandry` 的开发，重点实现牲畜和设备的CRUD，以及两者之间的绑定/解绑功能。
        *   完成前端 `AnimalList.vue` 和 `DeviceList.vue` 页面的联调。
    *   **第二步 (打通数据链路):**
        *   在 `jeecg-module-animal-husbandry` 中开发Kafka消费者，并与 ThingsBoard 对接，实现数据自动上报和入库TDengine。
        *   开发前端**牧场驾驶舱** (`dashboard/index.vue`)，实现地图上牲畜位置的实时更新。
    *   **第三步 (智能化升级):**
        *   开发 `jeecg-ai-service` 模块，并集成初步的基于规则的AI模型。
        *   开发前端**AI预警中心** (`alarm/AlarmCenter.vue`)。
    *   **第四步 (深化与完善):**
        *   开发统计报表、FOTA、设备监控等高级功能。
        *   引入更复杂的机器学习模型。
        *   优化前端交互和性能。
