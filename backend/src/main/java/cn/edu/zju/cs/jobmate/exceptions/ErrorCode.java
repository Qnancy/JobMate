package cn.edu.zju.cs.jobmate.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Custom error codes for {@link BusinessException} in JobMate.
 * 
 * @implNote Using 4 digits to represent different error types.
 */
public enum ErrorCode {

    // Unknown Errors.
    UNKNOWN_ERROR(0001, HttpStatus.INTERNAL_SERVER_ERROR, "未知错误"),

    // System Errors.
    INTERNAL_SERVER_ERROR(5000, HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误"),

    // Argument Errors.
    INVALID_PARAMETER(4001, HttpStatus.BAD_REQUEST, "参数无效"),
    MISSING_REQUIRED_PARAMETER(4002, HttpStatus.BAD_REQUEST, "缺少必需参数"),

    // Authentication Errors.
    UNAUTHORIZED(4010, HttpStatus.UNAUTHORIZED, "未授权访问"),

    // Permission Errors.
    FORBIDDEN(4030, HttpStatus.FORBIDDEN, "权限不足"),

    // Resource Errors.
    RESOURCE_NOT_FOUND(4040, HttpStatus.NOT_FOUND, "资源不存在"),
    RESOURCE_ALREADY_EXISTS(4090, HttpStatus.CONFLICT, "资源已存在"),
    RESOURCE_CONFLICT(4091, HttpStatus.CONFLICT, "资源冲突");

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(int code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public int getCode() { return code; }
    public HttpStatus getHttpStatus() { return httpStatus; }
    public String getMessage() { return message; }
}
