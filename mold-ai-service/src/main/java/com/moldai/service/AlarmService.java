package com.moldai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AlarmService {

    @Value("${mold-ai.alarm.enabled:false}")
    private boolean enabled;

    @Value("${mold-ai.alarm.webhook-url:}")
    private String webhookUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendAlarm(String title, String content) {
        if (!enabled) {
            return;
        }

        // 1. 总是打印日志
        log.error("ALARM [{}]: {}", title, content);

        // 2. 如果配置了 Webhook，发送请求 (以钉钉/飞书通用格式为例)
        if (webhookUrl != null && !webhookUrl.isEmpty()) {
            try {
                Map<String, Object> payload = new HashMap<>();
                payload.put("msgtype", "text");
                Map<String, String> text = new HashMap<>();
                text.put("content", "【霉菌AI告警】" + title + "\n" + content);
                payload.put("text", text);

                restTemplate.postForObject(webhookUrl, payload, String.class);
                log.info("Alarm sent to webhook successfully.");
            } catch (Exception e) {
                log.error("Failed to send webhook alarm: {}", e.getMessage());
            }
        }
    }
}

