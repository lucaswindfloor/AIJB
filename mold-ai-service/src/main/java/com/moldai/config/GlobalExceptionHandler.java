package com.moldai.config;

import com.moldai.controller.ApiResponse;
import com.moldai.service.AlarmService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private AlarmService alarmService;

    @ExceptionHandler(Exception.class)
    public ApiResponse<String> handleException(Exception e, HttpServletRequest request) {
        log.error("Unhandled exception: ", e);
        
        // 触发告警
        alarmService.sendAlarm("系统异常", 
            "Path: " + request.getRequestURI() + "\nError: " + e.getMessage());

        return ApiResponse.fail("System Error: " + e.getMessage());
    }
}

