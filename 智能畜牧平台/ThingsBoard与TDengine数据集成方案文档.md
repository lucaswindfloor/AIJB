# **ThingsBoard与TDengine数据集成方案**

## 1. 概述

### 1.1. 目标
本文档旨在详细阐述如何将 **ThingsBoard** 物联网平台上的、来自**多种异构设备**的遥测数据，通过 **Apache Kafka** 实时、无损地推送至后端 **Spring Boot** 服务，并最终以一种高度可扩展的“通用存储”模式持久化到 **TDengine** 时序数据库中。

### 1.2. 核心设计思想：关注点分离
本方案严格遵循“关注点分离”的设计原则：
- **Kafka (快递中转站)**: 作为纯粹的数据管道，负责高效、可靠地传输消息，**不关心、不解析**消息内部的数据结构。
- **数据消费层 (快递员)**: Kafka消费者仅负责从管道中取出消息，提取最必要的元数据（如`deviceName`），然后将**原始消息**完整地传递给下一层。
- **数据存储层 (仓库管理员)**: 负责将原始消息**原封不动**地存入数据库，保证数据的完整性和无损性。

这种设计确保了系统具有**极致的扩展性**。未来无论接入多少种新类型的设备，只要其数据能以JSON格式发送到Kafka，现有的数据接收和存储代码**无需任何修改**。

### 1.3. 技术架构与核心流程

- **技术栈**: `ThingsBoard` -> `Kafka` -> `Spring Boot (Jeecg-Boot)` -> `TDengine`
- **核心流程**:
  1.  **数据产生**: ThingsBoard 平台的多种设备（如瘤胃胶囊、动物追踪器）上报结构不同的遥测数据。
  2.  **数据路由**: ThingsBoard 内置的**规则链 (Rule Chain)** 捕获所有遥测数据。
  3.  **数据推送**: 规则链通过“Kafka”节点，将原始JSON数据发送到**同一个**Kafka Topic。
  4.  **数据消费与转发**: Spring Boot 应用中的 **Kafka消费者**监听到消息后，仅从中解析出`deviceName`，然后将**完整的原始JSON字符串**和`deviceName`一起转发给存储服务。
  5.  **通用化入库**: 存储服务接收到数据后，基于一个通用的**超级表 (STable)**，为每个设备自动创建子表，并将原始JSON字符串存入子表的`raw_data`列中。

![新架构图](https://mermaid.ink/svg/eyJjb2RlIjoiZ3JhcGggVERcbkFcVGhpbmdzQm9hcmRdIC0tPnxSYXcgdGVsb21ldHJ5IEpTT058IEIoUnVsZSBDaGFpbilcbkIoUnVsZSBDaGFpbikgLS0-fFB1c2ggdG8gS2FmZ2F8IENVbmlmaWVkIEthZmthIFRvcGljXVxuQ1VuaWZpZWQgS2Fma2EgVG9waWNdIC0tPnxDb25zdW1lICYgRm9yd2FyZCBbTm8gUGFyc2luZ10gfCBEW1NwcmluZyBCb290IEFwcF1cbkRbU3ByaW5nIEJvb3QgQXBwXSAtLT58SW5zZXJ0IFJBVyBKU09OfCBFW1REbmdpbmUgR2VuZXJpYyBTVEFibGVdXG4iLCJtZXJtYWlkIjp7InRoZW1lIjoiZGVmYXVsdCJ9LCJ1cGRhdGVFZGl0bXIiOmZhbHNlLCJhdXRvU3luYyI6dHJ1ZSwidXBkYXRlRGlhZ3JhbSI6ZmFsc2V9)

## 2. ThingsBoard 平台配置
配置保持不变，核心是确保所有需要存储的遥测数据都被推送到指定的Kafka Topic。

-   **Topic**: `telemetry_f0c794c0-e903-11ef-a8ee-99a8c68f9649`
-   **Bootstrap Servers**: `172.22.2.122:9092`

## 3. 后端Spring Boot应用配置

### 3.1. Maven 依赖 (`pom.xml`)
依赖保持不变，核心是 `spring-kafka` 和 `taos-jdbcdriver`。

### 3.2. Nacos 配置 (`jeecg-dev.yaml`)
Nacos配置也无需更改，确保`spring.kafka`和`spring.datasource.dynamic.datasource.tdengine`的配置正确无误。

### 3.3. Java 代码实现 (最终方案)

#### 3.3.1. 数据模型 (DTO) - （已废弃）
在新架构下，我们不再需要为每种设备创建特定的DTO（如`TelemetryMessage.java`, `AnimalTrackerMessage.java`等），因为我们不再解析消息的详细结构。

#### 3.3.2. TDengine 服务 (通用存储逻辑)
服务层的核心是创建一张通用的超级表，并提供一个接收原始JSON字符串的存储方法。

`TDengineTimeSeriesServiceImpl.java`:
```java
@Service
@Slf4j
public class TDengineTimeSeriesServiceImpl {
    // 1. 定义通用超级表的名称
    private static final String STABLE_NAME = "telemetry_data";

    @Autowired
    @Qualifier("tdengineJdbcTemplate")
    private JdbcTemplate tdengineJdbcTemplate;

    @PostConstruct
    public void init() {
        createSuperTable();
    }

    private void createSuperTable() {
        try {
            // 2. 定义通用表结构：时间戳 + 原始数据列 + 设备名标签
            // 注意：NCHAR最大长度为16374
            String sql = "CREATE STABLE IF NOT EXISTS " + STABLE_NAME + " (" +
                         "ts TIMESTAMP, " +
                         "raw_data NCHAR(16374)) " + 
                         "TAGS (device_name NCHAR(128))";
            tdengineJdbcTemplate.execute(sql);
            log.info("TDengine通用遥测超级表 '{}' 初始化成功或已存在。", STABLE_NAME);
        } catch (DataAccessException e) {
            log.error("TDengine通用遥测超级表 '{}' 初始化失败: {}", STABLE_NAME, e.getMessage(), e);
        }
    }
    
    // 3. 提供接收原始JSON的存储方法
    public void saveRawTelemetryData(String deviceName, String message) {
        if (deviceName == null || deviceName.isEmpty() || message == null || message.isEmpty()) {
            // ...
            return;
        }
        String tableName = "dev_" + deviceName.replaceAll("[^a-zA-Z0-9_]", "");
        try {
            // 确保子表存在
            createSubTableIfNotExists(tableName, deviceName);
            
            // 使用PreparedStatement防止SQL注入
            String sql = String.format("INSERT INTO %s (ts, raw_data) VALUES (now, ?)", tableName);
            tdengineJdbcTemplate.update(sql, message);
            
            log.info("成功将设备 '{}' 的原始遥测数据插入到TDengine表 '{}'。", deviceName, tableName);
        } catch (DataAccessException e) {
            log.error("插入原始遥测数据到TDengine失败: ...", e);
        }
    }

    private void createSubTableIfNotExists(String tableName, String deviceName) {
        // ...
    }
}
```

#### 3.3.3. Kafka 消费者服务 (纯粹的转发器)
消费者的逻辑被极大简化，只负责提取`deviceName`并转发。

`TelemetryConsumerService.java`:
```java
@Service
@Slf4j
public class TelemetryConsumerService {
    @Autowired
    private TDengineTimeSeriesServiceImpl tdengineService;
    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "telemetry_f0c794c0-e903-11ef-a8ee-99a8c68f9649", groupId = "animal-husbandry-group")
    public void consume(String message) {
        log.info("从Kafka主题接收到消息: {}", message);
        try {
            // 1. 唯一目的：提取deviceName
            JsonNode rootNode = objectMapper.readTree(message);
            JsonNode deviceNameNode = rootNode.path("deviceName");

            if (deviceNameNode.isMissingNode() || !deviceNameNode.isTextual()) {
                log.warn("Kafka消息中缺少deviceName字段，已忽略。");
                return;
            }
            String deviceName = deviceNameNode.asText();

            // 2. 调用服务，传入原始消息字符串进行存储
            tdengineService.saveRawTelemetryData(deviceName, message);

        } catch (Exception e) {
            log.error("处理遥测数据时发生未知错误: {}", e.getMessage(), e);
        }
    }
}
```

## 4. 关键问题排查 (Troubleshooting)
在整个集成过程中，我们遇到并解决了一系列典型问题，这些经验非常有价值。

1.  **问题：Druid SQL解析异常 `parse create error ... token IDENTIFIER STABLE`**
    -   **根因**: Druid的`StatFilter`默认会拦截并尝试解析所有SQL，但它不认识TDengine的`CREATE STABLE`语法。
    -   **解决**: 在Nacos中为`tdengine`数据源单独配置Druid属性，通过`filters: slf4j`覆盖全局配置，禁用掉`stat`和`wall`过滤器。

2.  **问题：TDengine建表语法错误 `Invalid column length for var length type`**
    -   **根因**: `CREATE STABLE`语句中，为`raw_data`列指定的`NCHAR(16384)`超过了TDengine规定的`16374`字节的最大长度。
    -   **解决**: 将列定义修改为`NCHAR(16374)`。

3.  **问题：TDengine建表逻辑冲突 `Table already exists in other stables`**
    -   **根因**: 在开发迭代过程中，数据库中残留了基于旧超级表创建的同名子表。当代码试图用新的超级表创建同名子表时，引发了冲突。
    -   **解决**: 手动连接TDengine数据库，使用`DROP TABLE <旧子表名>;`和`DROP STABLE <旧超级表名>;`命令清理历史遗留表。

4.  **问题：Kafka连接失败 `Connection to node -1 (localhost/127.0.0.1:9092) ...`**
    -   **根因**: Spring Boot应用未找到`spring.kafka.bootstrap-servers`配置项，使用了默认值`localhost:9092`。
    -   **解决**: 检查并确保Nacos `jeecg-dev.yaml`文件的`spring:`根节点下存在格式正确的`kafka`配置块。