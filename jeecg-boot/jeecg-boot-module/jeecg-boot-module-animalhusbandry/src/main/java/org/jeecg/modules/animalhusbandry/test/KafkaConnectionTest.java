package org.jeecg.modules.animalhusbandry.test;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaConnectionTest {

    public static void main(String[] args) {
        System.out.println("开始执行Kafka连接测试...");

        Properties props = new Properties();
        // 设置Kafka服务器地址
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.22.2.122:9092");
        // 设置消费者组ID，每次测试使用不同的ID以避免冲突
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "kafka-test-consumer-" + System.currentTimeMillis());
        // 设置Key和Value的反序列化器
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        // 从最早的偏移量开始消费
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        // 设置请求超时时间，以防网络不通长时间阻塞
        props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, "5000"); // 5秒
        props.put(ConsumerConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, "6000"); // 6秒

        // 要监听的主题
        String topic = "telemetry_f0c794c0-e903-11ef-a8ee-99a8c68f9649";
        System.out.println("准备连接Kafka服务器: " + props.getProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
        System.out.println("准备订阅主题: " + topic);

        Consumer<String, String> consumer = null;
        try {
            // 创建消费者实例
            consumer = new KafkaConsumer<>(props);
            System.out.println("消费者创建成功，正在订阅主题...");
            
            // 订阅主题
            consumer.subscribe(Collections.singletonList(topic));
            System.out.println("主题订阅成功，正在尝试拉取消息 (最多等待10秒)...");

            // 拉取消息
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(10));

            if (records.isEmpty()) {
                System.out.println("\n测试结果: 成功连接到Kafka，但主题 '" + topic + "' 中没有消息或在10秒内没有新消息。");
                System.out.println("这表明网络和配置是通的，但需要确认ThingsBoard是否真的在推送数据。");
            } else {
                System.out.println("\n测试结果: 成功连接到Kafka并成功接收到 " + records.count() + " 条消息！");
                System.out.println("Kafka服务工作正常，主题中有数据。");
                records.forEach(record -> {
                    System.out.println("--------------------");
                    System.out.println("收到消息: " + record.value());
                    System.out.println("所在分区: " + record.partition() + ", 偏移量: " + record.offset());
                });
            }

        } catch (Exception e) {
            System.err.println("\n测试结果: Kafka连接或消费失败！");
            System.err.println("错误类型: " + e.getClass().getName());
            System.err.println("错误信息: " + e.getMessage());
            System.err.println("请检查：");
            System.err.println("1. 网络是否通畅，能否访问 " + props.getProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
            System.err.println("2. Kafka服务是否正在运行。");
            System.err.println("3. 主题名称 '" + topic + "' 是否正确。");
        } finally {
            if (consumer != null) {
                // 关闭消费者
                consumer.close();
                System.out.println("\n消费者已关闭，测试结束。");
            }
        }
    }
}
