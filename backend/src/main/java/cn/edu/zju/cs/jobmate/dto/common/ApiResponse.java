package cn.edu.zju.cs.jobmate.dto.common;

import cn.edu.zju.cs.jobmate.exceptions.ErrorCode;
import lombok.Builder;
import lombok.Data;

/**
 * Unified API response wrapper class
 * 
 * @param <T> Response data type
 * @apiNote This class will be set in HTTP response body as JSON format.
 */
@Data
@Builder
public class ApiResponse<T> {

    private Integer code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> ok() {
        return new ApiResponse<>(200, "success", null);
    }

    public static <T> ApiResponse<T> ok(String message) {
        return new ApiResponse<>(200, message, null);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    public static <T> ApiResponse<T> error(Integer code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    public static <T> ApiResponse<T> error(Integer code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return new ApiResponse<>(
            errorCode.getHttpStatus().value(),
            errorCode.getMessage(),
            null
        );
    }

    public Integer getCode() { return code; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}
