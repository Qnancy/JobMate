package cn.edu.zju.cs.jobmate.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;

/**
 * Unified API response wrapper class
 * 
 * @param <T> Response data type
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    private Integer code;
    private String message;
    private T data;

    /**
     * Success response (HTTP 200)
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "Operation successful", data);
    }

    /**
     * Success response (HTTP 200) with custom message
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    /**
     * Success response (HTTP 200) without data
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "Operation successful", null);
    }

    /**
     * Success response (HTTP 200) with custom message, without data
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(200, message, null);
    }

    /**
     * Error response
     */
    public static <T> ApiResponse<T> error(Integer code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    /**
     * Error response (HTTP 400)
     */
    public static <T> ApiResponse<T> badRequest(String message) {
        return new ApiResponse<>(400, message, null);
    }

    /**
     * Error response (HTTP 404)
     */
    public static <T> ApiResponse<T> notFound(String message) {
        return new ApiResponse<>(404, message, null);
    }

    /**
     * Error response (HTTP 500)
     */
    public static <T> ApiResponse<T> internalError(String message) {
        return new ApiResponse<>(500, message, null);
    }
}
