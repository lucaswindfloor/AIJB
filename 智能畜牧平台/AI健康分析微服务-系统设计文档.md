# AI健康分析微服务 - 系统设计文档 (SDD)

## 1. 概述

### 1.1. 项目背景
本项目旨在为“智能畜牧管理平台”提供核心的AI健康分析能力。当前平台对于牲畜的健康状态、评分及结论均为模拟数据，缺乏真实的后台分析支撑。本微服务旨在替代模拟数据，通过对采集的生理指标（体温、活动量等）进行后台分析，生成真实的、有价值的健康评估，最终实现从“被动治疗”到“主动预防”的业务目标。

### 1.2. 设计目标
- **构建一个独立的、高内聚的AI健康分析微服务** (`jeecg-ai-service`)。
- **与现有业务系统（如 `jeecg-animalhusbandry`）完全解耦**，除共享数据库外，无任何代码级别的依赖。
- 通过**定时任务**（XXL-Job）异步执行分析，避免对实时业务造成性能影响。
- MVP（最小可行产品）版本将实现一个**基于专家规则的分析引擎**，为后续迭代更复杂的机器学习模型奠定基础。

## 2. 系统架构

### 2.1. 架构图

```mermaid
graph TD
    subgraph 外部数据源
        A[TDengine: 存储生理时序数据]
        B[MySQL: 存储业务数据]
    end

    subgraph 新建: AI健康分析微服务 (jeecg-ai-service)
        C[XXL-Job: 调度器] --> D{健康分析任务: HealthAnalysisJob};
        D --> E[分析服务: HealthAnalysisService];
        E --> F[TDengine数据服务];
        F -- "1. 拉取生理数据" --> A;
        D -- "2. 获取绑定关系" --> G[MySQL数据访问: Mapper];
        G -- "SELECT FROM ah_animal_device_link" --> B;
        E -- "3. 更新分析结果" --> G;
        G -- "UPDATE ah_animal" --> B;
    end

    subgraph 现有: 智能畜牧微服务 (jeecg-animalhusbandry)
        H[畜牧业务API] -- "用户请求" --> I[牲畜档案服务];
        I -- "SELECT FROM ah_animal" --> B;
    end

    J[Nacos: 服务注册与发现]
    subgraph 基础设施
        J
    end

    C -.-> J
    H -.-> J

```

### 2.2. 核心设计原则
- **独立部署**: `jeecg-ai-service` 是一个独立的Spring Boot应用，可以独立于其他服务进行部署、升级和伸缩。
- **服务注册**: `jeecg-ai-service` 将作为独立的客户端注册到Nacos，便于统一监控和管理。
- **数据驱动**: 与现有系统的交互完全通过数据库进行。AI服务作为**数据生产者**，将分析结果写入MySQL；畜牧服务作为**数据消费者**，从MySQL读取数据，两者互不直接调用。
- **无代码耦合**: AI服务的Maven项目中，**绝不包含**对`jeecg-boot-module-animalhusbandry`或其他任何业务模块的依赖。所有需要的数据实体（如`AhAnimal`）都将在AI服务内部重新定义。

## 3. 模块设计

本微服务将遵循Jeecg-Boot的标准规范，由两个新的Maven模块组成：

1.  **业务逻辑模块**: `jeecg-boot-module-ai-service`
    - **位置**: `jeecg-boot/jeecg-boot-module/`
    - **职责**: 存放所有核心业务逻辑。
    - **内部包结构**:
        - `entity`: 定义数据实体类，如 `AhAnimal`, `AhAnimalDeviceLink`。**这些是本模块私有的，从现有模块复制而来，以实现解耦。**
        - `mapper`: 定义MyBatis Plus的Mapper接口，用于访问MySQL的`ah_animal`和`ah_animal_device_link`表。
        - `service`: 核心服务层，包含访问TDengine的逻辑和规则分析引擎。
        - `job`: XXL-Job的任务处理器。

2.  **微服务启动模块**: `jeecg-ai-service-cloud-start`
    - **位置**: `jeecg-boot/jeecg-server-cloud/`
    - **职责**: 作为Spring Boot启动器，打包业务逻辑模块，配置Nacos客户端，并以`jeecg-ai-service`的名称注册为微服务。

## 4. 核心数据流

1.  **任务触发**: XXL-Job调度中心按预设的Cron表达式（例如，每小时一次）触发`HealthAnalysisJob`。
2.  **获取任务列表**: `HealthAnalysisJob`调用`AnimalDeviceLinkMapper`，从MySQL的`ah_animal_device_link`表中查询所有`is_active = 1`的记录，获取需要分析的`animal_id`和`device_id`的配对列表。
3.  **拉取时序数据**: 遍历任务列表，对于每一个`device_id`，`HealthAnalysisService`调用`TDengineService`，从TDengine中拉取过去一段时间（例如24小时）的生理指标数据。
4.  **执行分析**: `HealthAnalysisService`内的规则引擎对拉取到的时序数据进行分析，计算出`healthStatus`（健康状态）、`healthScore`（健康评分）、`aiConclusion`（AI结论）。
5.  **回写结果**: `HealthAnalysisService`调用`AnimalHealthMapper`，将生成的三个分析结果通过`animal_id`更新回MySQL的`ah_animal`表中。

## 5. 接口设计

- **入站接口**: 本服务**不提供任何RESTful API**。唯一的入口点是`HealthAnalysisJob`，由XXL-Job调度中心触发。
- **出站接口**:
    - `JDBC to MySQL`: 连接业务数据库，读写`ah_animal`和`ah_animal_device_link`表。
    - `JDBC to TDengine`: 连接时序数据库，读取`bolus_data`超级表下的子表。

## 6. MVP实施规划
- **规则引擎**: 第一版分析逻辑将基于硬编码的专家规则（如体温阈值、活动量阈值等）。
- **配置**: 规则的阈值未来可考虑放入Nacos配置中心，实现动态调整。
- **数据表**: 依赖的MySQL表`ah_animal`, `ah_animal_device_link`为现有表，本服务不新建表。

---
*本文档确认，AI健康分析微服务将作为一个完全独立的系统进行设计和开发，确保与现有系统的松耦合和高内聚。*
