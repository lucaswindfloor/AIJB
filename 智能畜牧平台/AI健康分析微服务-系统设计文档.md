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

## 7. 附录：核心问题排查与解决过程复盘
在AI分析微服务的联调测试过程中，我们遇到并解决了一系列典型问题，覆盖了从任务调度、数据源切换到具体业务逻辑的多个层面。本附录旨在记录这些问题的现象、根源及最终解决方案，为后续的维护和迭代提供宝贵经验。

### 7.1. 问题一：调度失败 - 连接超时 (`Read timed out`)
- **现象**: 在XXL-Job界面手动执行任务时，日志显示“调度结果：失败”，但稍后“执行结果：成功”。调度备注中出现 `Read timed out` 错误。
- **根源分析**: AI分析任务（特别是首次运行时）涉及大量的数据查询和计算，总执行耗时可能超过XXL-Job调度器默认的HTTP请求超时时间（通常为10-30秒）。调度器因未能及时收到执行器的HTTP响应而提前判定“调度失败”，但执行器本身仍在后台继续执行任务并最终成功。
- **解决方案**: 在XXL-Job的“任务管理”界面，编辑该任务，在“高级配置”中将**“任务超时时间”**设置为一个更长的值（例如 `120` 秒），确保调度器有足够的时间等待任务执行完成。

### 7.2. 问题二：数据源切换失败 (MySQL `Unknown error 1146`)
- **现象**: AI服务在尝试查询TDengine时，后台日志抛出来自**MySQL驱动**的`java.sql.SQLSyntaxErrorException: Unknown error 1146` (表不存在) 错误。
- **根源分析**: 调用链始于`HealthAnalysisServiceImpl`中带`@Transactional`注解的方法。该注解导致Spring提前开启了基于默认数据源（MySQL）的事务。当内部代码尝试调用带`@DS("tdengine")`注解的方法时，由于已存在活跃的MySQL事务，**事务的优先级高于数据源切换**，导致`@DS`注解失效，查询被错误地发送到了MySQL。
- **解决方案**: 在`TDengineServiceImpl`的查询方法上，添加注解`@Transactional(propagation = Propagation.NOT_SUPPORTED)`。该配置会**挂起**外部的MySQL事务，允许数据源临时切换到TDengine，执行查询后再恢复原事务。

### 7.3. 问题三：TDengine表名错误 (TDengine `Table does not exist`)
- **现象**: 解决了数据源切换问题后，日志中抛出来自**TDengine驱动**的`java.sql.SQLException: Table does not exist`错误。
- **根源分析**: 对数据关系理解有误。`HealthAnalysisJob`从`ah_animal_device_link`表中获取的`device_id`是`ah_device`表的**主键**，而TDengine中的子表名实际上是由`ah_device`表中的**`dev_eui`**字段拼接而成的。代码错误地将主键ID用作了`dev_eui`。
- **解决方案**: 重构`HealthAnalysisJob`。在获取到`device_id`后，增加一步数据库查询：先根据`device_id`查询`ah_device`表，获取到正确的`dev_eui`字符串，然后再将这个`dev_eui`传递给分析服务用于后续的TDengine查询。

### 7.4. 问题四：TDengine数据结构与时间戳错误
- **现象**: 成功查询到TDengine表后，获取不到数据或数据分析结果不符合预期。
- **根源分析**: 存在两个关键的错误假设：
    1.  **结构错误**: 假设`temperature`、`step`等生理指标是独立的列，而实际上它们都封装在一个名为`raw_data`的JSON字符串中。
    2.  **时间戳错误**: 依赖TDengine表自带的`ts`主键列进行时间过滤。该列记录的是**数据入库时间**，而非设备上报的**真实事件时间**。由于Kafka存在数据缓冲，两者可能存在显著差异。
- **解决方案**:
    1.  **修正SQL**: 修改`TDengineServiceImpl`中的SQL语句，不再查询独立的生理指标列，而是直接查询`raw_data`列。
    2.  **采用“先捞取、后过滤”策略**: 在`HealthAnalysisServiceImpl`中，首先从TDengine获取一个较宽时间范围（如过去30天）的`raw_data`列表。然后在Java内存中，逐条解析JSON，获取JSON内部真实的`ts`字段（事件时间戳），并用代码逻辑精确过滤出属于过去24小时的数据，再进行后续的分析计算。这确保了AI分析所用数据在时间上的高精度。


---
*本文档确认，AI健康分析微服务将作为一个完全独立的系统进行设计和开发，确保与现有系统的松耦合和高内聚。*
