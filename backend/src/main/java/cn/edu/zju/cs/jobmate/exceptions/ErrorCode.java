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
    INVALID_PARAMETER(2001, HttpStatus.BAD_REQUEST, "参数无效"),
    MISSING_PARAMETER(2002, HttpStatus.BAD_REQUEST, "缺少参数"),

    // Authentication Errors.
    UNAUTHORIZED(4010, HttpStatus.UNAUTHORIZED, "未授权访问"),

    // Permission Errors.
    FORBIDDEN(4030, HttpStatus.FORBIDDEN, "权限不足"),

    // General Resource Errors.
    TOO_MANY_REQUESTS(5001, HttpStatus.TOO_MANY_REQUESTS, "请求过于频繁，请稍后再试"),
    RESOURCE_CONFLICT(4091, HttpStatus.CONFLICT, "资源冲突"),
    
    // User Errors.
    USER_NOT_FOUND(4041, HttpStatus.NOT_FOUND, "用户不存在"),
    USER_ALREADY_EXISTS(4092, HttpStatus.CONFLICT, "用户已存在"),
    
    // Company Errors.
    COMPANY_NOT_FOUND(4042, HttpStatus.NOT_FOUND, "公司不存在"),
    COMPANY_ALREADY_EXISTS(4093, HttpStatus.CONFLICT, "公司已存在"),
    
    // Job Info Errors.
    JOB_INFO_NOT_FOUND(4043, HttpStatus.NOT_FOUND, "招聘信息不存在"),
    JOB_INFO_ALREADY_EXISTS(4094, HttpStatus.CONFLICT, "招聘信息已存在"),
    
    // Activity Info Errors.
    ACTIVITY_INFO_NOT_FOUND(4044, HttpStatus.NOT_FOUND, "活动信息不存在"),
    ACTIVITY_INFO_ALREADY_EXISTS(4095, HttpStatus.CONFLICT, "活动信息已存在"),
    
    // Subscription Errors.
    JOB_SUBSCRIPTION_NOT_FOUND(4045, HttpStatus.NOT_FOUND, "招聘订阅不存在"),
    JOB_SUBSCRIPTION_ALREADY_EXISTS(4096, HttpStatus.CONFLICT, "招聘订阅已存在"),
    ACTIVITY_SUBSCRIPTION_NOT_FOUND(4046, HttpStatus.NOT_FOUND, "活动订阅不存在"),
    ACTIVITY_SUBSCRIPTION_ALREADY_EXISTS(4097, HttpStatus.CONFLICT, "活动订阅已存在");

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

    @Override
    public String toString() {
        return "ErrorCode{" +
               "code=" + code +
               ", httpStatus=" + httpStatus +
               ", message='" + message + '\'' +
               '}';
    }
}
