package org.jeecg.modules.parking.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.parking.config.ThingsboardConfig;
import org.jeecg.modules.parking.dto.ThingsboardTokenResponse;
import org.jeecg.modules.parking.service.IThingsboardAuthService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class ThingsboardAuthServiceImpl implements IThingsboardAuthService {

    private final ThingsboardConfig thingsboardConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final AtomicReference<ThingsboardTokenResponse> tokenCache = new AtomicReference<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public ThingsboardAuthServiceImpl(ThingsboardConfig thingsboardConfig, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.thingsboardConfig = thingsboardConfig;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    private void init() {
        login();
        scheduleTokenRefresh();
    }
    
    @Override
    public String getAccessToken() {
        ThingsboardTokenResponse tokens = tokenCache.get();
        if (tokens == null || isTokenExpired(tokens.getToken())) {
            log.warn("Access Token is null or expired, forcing login.");
            login();
        }
        return tokenCache.get().getToken();
    }

    private void login() {
        String url = thingsboardConfig.getHost() + "/api/auth/login";
        log.info("Attempting to log in to ThingsBoard at {}", url);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            String requestJson = String.format("{\"username\":\"%s\", \"password\":\"%s\"}",
                    thingsboardConfig.getUsername(), thingsboardConfig.getPassword());

            HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
            ResponseEntity<ThingsboardTokenResponse> response = restTemplate.postForEntity(url, entity, ThingsboardTokenResponse.class);

            if (response.getBody() != null) {
                tokenCache.set(response.getBody());
                log.info("Successfully logged in to ThingsBoard. Token cached.");
            } else {
                 throw new RuntimeException("ThingsBoard login response body is null");
            }
        } catch (HttpClientErrorException e) {
            log.error("Failed to log in to ThingsBoard. Status: {}, Body: {}", e.getRawStatusCode(), e.getResponseBodyAsString(), e);
            throw new RuntimeException("Failed to log in to ThingsBoard: " + e.getMessage());
        }
    }
    
    private void refreshToken() {
        ThingsboardTokenResponse currentTokens = tokenCache.get();
        if (currentTokens == null || currentTokens.getRefreshToken() == null) {
            log.warn("No refresh token available. Performing full login.");
            login();
            return;
        }

        String url = thingsboardConfig.getHost() + "/api/auth/token";
        log.info("Attempting to refresh ThingsBoard token at {}", url);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            
            String requestJson = String.format("{\"refreshToken\":\"%s\"}", currentTokens.getRefreshToken());

            HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
            ResponseEntity<ThingsboardTokenResponse> response = restTemplate.postForEntity(url, entity, ThingsboardTokenResponse.class);

            if (response.getBody() != null) {
                tokenCache.set(response.getBody());
                log.info("Successfully refreshed ThingsBoard token. New token cached.");
            } else {
                 throw new RuntimeException("ThingsBoard token refresh response body is null");
            }
        } catch (Exception e) {
            log.error("Failed to refresh ThingsBoard token. Re-logging in.", e);
            // If refresh fails, try a full login as a fallback
            login();
        }
    }

    private boolean isTokenExpired(String token) {
        if (token == null) {
            return true;
        }
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                log.warn("Invalid JWT token format.");
                return true;
            }
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            JsonNode payloadNode = objectMapper.readTree(payload);
            long exp = payloadNode.get("exp").asLong();
            long currentTimeSeconds = System.currentTimeMillis() / 1000;
            // Add a buffer of 60 seconds
            return exp < (currentTimeSeconds + 60);
        } catch (IOException | NullPointerException e) {
            log.error("Error checking token expiration", e);
            return true;
        }
    }
    
    private void scheduleTokenRefresh() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                ThingsboardTokenResponse tokens = tokenCache.get();
                if (tokens != null && isTokenExpired(tokens.getToken())) {
                    log.info("Access token is about to expire, attempting to refresh it.");
                    refreshToken();
                } else if (tokens == null) {
                    log.warn("Token cache is empty, attempting to login.");
                    login();
                }
            } catch (Exception e) {
                log.error("Error during scheduled token refresh check", e);
            }
        }, 1, 1, TimeUnit.MINUTES);
    }
} 