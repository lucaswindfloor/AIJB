package com.moldai.controller;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiResponse<T> {
    private Integer code;
    private String message;
    private T result;
    private long timestamp = System.currentTimeMillis();

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> r = new ApiResponse<>();
        r.setCode(200);
        r.setResult(data);
        r.setMessage("Success");
        return r;
    }
    
    public static <T> ApiResponse<T> fail(String message) {
        ApiResponse<T> r = new ApiResponse<>();
        r.setCode(500);
        r.setMessage(message);
        return r;
    }
}


