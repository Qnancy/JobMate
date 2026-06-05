package cn.edu.zju.cs.jobmate.exceptions;

/**
 * Business exception for JobMate application.
 * 
 * @apiNote Using {@link ErrorCode} enum to represent specific error scenarios.
 */
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * Constructor.
     * 
     * @param errorCode ErrorCode enum.
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * Constructor with cause.
     * 
     * @param errorCode ErrorCode enum.
     * @param cause     The original exception cause.
     */
    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    /**
     * Constructor with a custom message (keeps {@link ErrorCode} for HTTP mapping).
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
