package cn.edu.zju.cs.jobmate.exceptions;

/**
 * Custom error codes for {@link BusinessException} in JobMate.
 * 
 * @implNote Using 4 digits to represent different error types.
 */
public enum ErrorCode {

    // Unknown Errors.
    UNKNOWN_ERROR(0001, "未知错误");

    // System Errors.

    // Argument Errors.

    // Authentication Errors.

    // Permission Errors.

    // Resource Errors.

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() { return code; }
    public String getMessage() { return message; }
}