package org.jeecg.modules.animalhusbandry.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.animalhusbandry.service.IThingsBoardCapsuleService;
import org.jeecg.modules.parking.config.ThingsboardConfig;
import org.jeecg.modules.parking.service.IThingsboardAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Slf4j
@Service
public class ThingsBoardCapsuleServiceImpl implements IThingsBoardCapsuleService {

    @Autowired
    private IThingsboardAuthService thingsboardAuthService;

    @Autowired
    private ThingsboardConfig thingsboardConfig;

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Result<?> getLatestTelemetry(String tbDeviceId) {
        String accessToken = thingsboardAuthService.getAccessToken();
        if (accessToken == null) {
            return Result.error("获取遥测数据失败：未获取到ThingsBoard访问令牌");
        }

        HttpUrl.Builder urlBuilder = HttpUrl.parse(thingsboardConfig.getHost() + "/api/plugins/telemetry/DEVICE/" + tbDeviceId + "/values/timeseries").newBuilder();
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .header("X-Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("Failed to get telemetry data from ThingsBoard for device {}: {}", tbDeviceId, response.body() != null ? response.body().string() : "Empty Response");
                return Result.error("从ThingsBoard获取遥测数据失败，状态码: " + response.code());
            }

            String responseBody = response.body().string();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            
            Map<String, Object> telemetryMap = new HashMap<>();
            long latestTimestamp = 0L;

            Iterator<Map.Entry<String, JsonNode>> fields = rootNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String key = field.getKey();
                JsonNode valueNode = field.getValue();

                if (valueNode.isArray() && valueNode.size() > 0) {
                    JsonNode latestEntry = valueNode.get(0);
                    if (latestEntry.has("value")) {
                        String camelCaseKey = Character.toLowerCase(key.charAt(0)) + key.substring(1);
                        telemetryMap.put(camelCaseKey, latestEntry.get("value").asText("N/A"));
                    }
                    if (latestEntry.has("ts")) {
                        long currentTs = latestEntry.get("ts").asLong();
                        if (currentTs > latestTimestamp) {
                            latestTimestamp = currentTs;
                        }
                    }
                }
            }

            if (latestTimestamp > 0) {
                telemetryMap.put("lastHeartbeatTime", latestTimestamp);
            }
            
            return Result.OK(telemetryMap);
        } catch (IOException e) {
            log.error("Error getting telemetry data from ThingsBoard for device {}", tbDeviceId, e);
            return Result.error("调用ThingsBoard接口异常");
        }
    }
} 