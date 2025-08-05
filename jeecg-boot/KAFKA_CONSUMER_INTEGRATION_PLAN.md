# JeecgBoot集成Kafka消费ThingsBoard数据并存入TDengine实施方案

## 1. 目标

本方案旨在打通从ThingsBoard到TDengine的实时数据链路。通过在JeecgBoot后端项目中实现一个Kafka消费者，自动消费由ThingsBoard推送的设备遥测数据，并将其持久化到TDengine数据库中。

**数据流:** `设备` -> `ThingsBoard` -> `Kafka Topic` -> `JeecgBoot Kafka Consumer` -> `TDengine`

这将彻底取代前端轮询拉取数据的低效模式，实现数据的实时推送和处理。

## 2. 实施步骤

我们将分步在`jeecg-boot-module-animalhusbandry`模块中完成此功能。

### 第1步：添加Maven依赖

首先，我们需要为项目引入`spring-kafka`库，以便使用Spring提供的Kafka消费者功能。

**文件路径:** `e:\download\AIJB-main\jeecg-boot\jeecg-boot-module\jeecg-boot-module-animalhusbandry\pom.xml`

**操作:** 在`<dependencies>`标签内，添加以下内容：

```xml
        <!-- Spring Kafka Support -->
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
        </dependency>
```

### 第2步：配置Kafka连接信息

接下来，我们需要告诉应用程序Kafka服务器的地址。

**文件路径:** `e:\download\AIJB-main\jeecg-boot\jeecg-module-system\jeecg-system-start\src\main\resources\application-dev.yml`

**操作:** 在`spring:`配置块下（例如，在`redis:`配置之后），添加以下Kafka配置：

```yaml
  #kafka 配置
  kafka:
    bootstrap-servers: 172.22.3.102:9092,172.22.3.102:9093
    consumer:
      # 当Kafka中没有初始偏移量，或当前偏移量在服务器上不再存在时:
      # earliest: 自动将偏移量重置为最早的偏移量
      # latest: 自动将偏移量重置为最新的偏移量
      # none: 抛出异常
      auto-offset-reset: earliest
      # 是否自动提交偏移量，默认值是true。为保证消息不丢失，建议设置为false，手动提交。
      enable-auto-commit: false
    listener:
      # 在侦听器容器中运行的线程数
      concurrency: 5
      # AckMode: RECORD, 每处理一条消息后自动提交偏移量
      ack-mode: record
```

### 第3步：创建Kafka消费者服务

这是核心步骤。我们将创建一个Java类来监听Kafka Topic并处理接收到的消息。

**操作:** 创建一个新的Java文件。

*   **文件路径:** `e:\download\AIJB-main\jeecg-boot\jeecg-boot-module\jeecg-boot-module-animalhusbandry\src\main\java\org\jeecg\modules\animalhusbandry\kafka\CapsuleTelemetryConsumer.java`
*   **文件内容:**

```java
package org.jeecg.modules.animalhusbandry.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;

/**
 * Kafka consumer for receiving telemetry data from ThingsBoard and saving it to TDengine.
 */
@Component
@Slf4j
public class CapsuleTelemetryConsumer {

    // 监听的Kafka Topic，建议从配置文件读取
    // 注意：这里的租户ID需要替换成您自己的
    private static final String KAFKA_TOPIC = "telemetry_48d24f10-223a-11ef-a8ee-99a8c68f9649";
    private static final String KAFKA_GROUP_ID = "jeecg-animal-husbandry-consumer";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Listens for messages on the specified Kafka topic.
     *
     * @param message the message payload received from Kafka.
     */
    @KafkaListener(topics = KAFKA_TOPIC, groupId = KAFKA_GROUP_ID)
    public void listen(String message) {
        log.info("Received message from Kafka topic [{}]: {}", KAFKA_TOPIC, message);

        try {
            // 1. 解析JSON消息
            JsonNode rootNode = objectMapper.readTree(message);
            String deviceName = rootNode.get("deviceName").asText(); // ThingsBoard设备名称，如 DEV-CAP-001
            String dataStr = rootNode.get("data").asText(); // 遥测数据是嵌套的JSON字符串
            JsonNode dataNode = objectMapper.readTree(dataStr);

            // 2. 构建SQL语句
            // TDengine的表名约定为: telemetry_{deviceName}
            // 注意：直接拼接字符串有SQL注入风险，实际生产应做更严格的校验
            String tableName = "telemetry_" + deviceName.replace("-", "_").toLowerCase();
            
            StringBuilder columns = new StringBuilder("ts");
            StringBuilder values = new StringBuilder("NOW"); // 使用TDengine服务器时间
            
            Iterator<Map.Entry<String, JsonNode>> fields = dataNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String key = field.getKey();
                String value = field.getValue().asText();
                
                // 过滤掉无效数据或空值
                if (value != null && !value.isEmpty() && !"null".equalsIgnoreCase(value)) {
                    columns.append(", ").append(key);
                    // 判断值是否为字符串类型，如果是则加上单引号
                    if (field.getValue().isTextual()) {
                        values.append(", '").append(value).append("'");
                    } else {
                        values.append(", ").append(value);
                    }
                }
            }

            String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columns.toString(), values.toString());
            log.info("Executing TDengine SQL: {}", sql);

            // 3. 执行SQL插入
            jdbcTemplate.execute(sql);
            log.info("Successfully inserted telemetry data for device [{}] into table [{}]", deviceName, tableName);

        } catch (JsonProcessingException e) {
            log.error("Failed to parse Kafka message JSON: {}", message, e);
        } catch (Exception e) {
            log.error("Failed to process Kafka message and save to TDengine: {}", message, e);
        }
    }
}
```
**注意:**
*   上面的代码直接包含了将数据写入TDengine的逻辑。它依赖于Spring的`JdbcTemplate`，JeecgBoot已经配置好了。
*   代码中包含了对ThingsBoard遥测数据格式的解析。
*   `KAFKA_TOPIC`中的租户ID `48d24f10-223a-11ef-a8ee-99a8c68f9649` 是一个示例，您需要替换成您自己环境的真实ID。

## 3. 如何构建和测试

1.  **应用修改**: 将上述三个步骤中的代码和配置应用到您的项目中。
2.  **修改规则链**: 确保您的ThingsBoard规则链（无论是`瘤胃胶囊规则链`还是`车位锁规则链`）最终都将数据推送到Kafka中名为 `telemetry_{tenant_id}` 的Topic。
3.  **构建项目**: 在`jeecg-boot`根目录下，执行Maven构建命令：
    ```bash
    mvn clean install -DskipTests
    ```
4.  **运行项目**: 启动JeecgBoot主应用程序。
5.  **触发数据**: 让您的物理设备（如瘤胃胶囊）上报一条数据到ThingsBoard。
6.  **检查日志**: 查看JeecgBoot应用的控制台日志。您应该能看到：
    *   `Received message from Kafka topic...` 的日志，证明已从Kafka收到数据。
    *   `Executing TDengine SQL...` 的日志，显示了将要执行的SQL语句。
    *   `Successfully inserted telemetry data...` 的日志，证明数据已成功写入TDengine。
7.  **验证数据库**: 登录TDengine，查询对应的设备表，确认新数据已写入。
