package cn.edu.zju.cs.jobmate.exceptions;

/**
 * Business exception for JobMate application.
 * 
 * @apiNote Using {@link ErrorCode} enum to represent specific error scenarios.
 */
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
