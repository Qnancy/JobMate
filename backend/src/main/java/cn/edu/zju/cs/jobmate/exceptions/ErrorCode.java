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

    // Argument Errors.

    // Authentication Errors.

    // Permission Errors.

    // Resource Errors.
    TOO_MANY_REQUESTS(5001, HttpStatus.TOO_MANY_REQUESTS, "请求过于频繁，请稍后再试");

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
