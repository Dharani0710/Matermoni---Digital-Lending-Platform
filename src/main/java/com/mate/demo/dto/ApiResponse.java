package com.mate.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {
    private boolean success;
    private String message;
    private LocalDateTime timestamp;
    private Object data;
    private Map<String, String> errors;

    // Success responses
    public ApiResponse(boolean success, String message) {
        this(success, message, null, null);
    }

    public ApiResponse(boolean success, String message, Object data) {
        this(success, message, data, null);
    }

    private ApiResponse(boolean success, String message, Object data, Map<String, String> errors) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }

    // Error response
    public static ApiResponse error(String message) {
        return new ApiResponse(false, message);
    }

    public static ApiResponse error(String message, Map<String, String> errors) {
        return new ApiResponse(false, message, null, errors);
    }

    // Success response helpers
    public static ApiResponse ok(String message) {
        return new ApiResponse(true, message);
    }

    public static ApiResponse ok(String message, Object data) {
        return new ApiResponse(true, message, data);
    }
}