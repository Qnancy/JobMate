package cn.edu.zju.cs.jobmate.exceptions;

import org.springframework.http.HttpStatus;

import cn.edu.zju.cs.jobmate.utils.log.ToStringUtil;

/**
 * Custom error codes for {@link BusinessException} in JobMate.
 * 
 * @implNote Using 4 digits to represent different error types.
 */
public enum ErrorCode {

    // System Errors.
    INTERNAL_SERVER_ERROR(1001, HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误"),
    TOKEN_SIGNING_ERROR  (1002, HttpStatus.INTERNAL_SERVER_ERROR, "令牌签名错误"),
    TOKEN_PARSING_ERROR  (1003, HttpStatus.INTERNAL_SERVER_ERROR, "令牌解析错误"),

    // Argument Errors.
    INVALID_PARAMETER(2001, HttpStatus.BAD_REQUEST, "非法或无效的参数"),
    MISSING_PARAMETER(2002, HttpStatus.BAD_REQUEST, "缺少参数"),
    NO_UPDATES       (2003, HttpStatus.BAD_REQUEST, "没有更新"),

    // Authentication Errors.
    UNAUTHENTICATED       (3001, HttpStatus.UNAUTHORIZED, "未认证访问"),
    AUTHENTICATION_FAILED (3002, HttpStatus.UNAUTHORIZED, "认证失败"),
    INVALID_TOKEN         (3003, HttpStatus.UNAUTHORIZED, "无效或过期的令牌"),
    INVALID_AUTHENTICATION(3004, HttpStatus.UNAUTHORIZED, "用户名或密码错误"),

    // Permission Errors.
    INVALID_ADMIN_SECRET(4001, HttpStatus.FORBIDDEN, "无效的管理员密钥"),
    FORBIDDEN_ACCESS    (4002, HttpStatus.FORBIDDEN, "拒绝访问"),

    // General Resource Errors.
    TOO_MANY_REQUESTS(5001, HttpStatus.TOO_MANY_REQUESTS, "请求过于频繁，请稍后再试"),

    // Business Resource Errors.
    ACTIVITY_INFO_ALREADY_EXISTS        (4001, HttpStatus.CONFLICT, "活动信息已存在"),
    ACTIVITY_SUBSCRIPTION_ALREADY_EXISTS(4002, HttpStatus.CONFLICT, "活动订阅已存在"),
    COMPANY_ALREADY_EXISTS              (4003, HttpStatus.CONFLICT, "企业已存在"),
    JOB_INFO_ALREADY_EXISTS             (4004, HttpStatus.CONFLICT, "招聘信息已存在"),
    JOB_SUBSCRIPTION_ALREADY_EXISTS     (4005, HttpStatus.CONFLICT, "招聘订阅已存在"),
    USER_ALREADY_EXISTS                 (4006, HttpStatus.CONFLICT, "用户已存在"),
    ACTIVITY_INFO_NOT_FOUND             (4007, HttpStatus.NOT_FOUND, "活动信息不存在"),
    ACTIVITY_SUBSCRIPTION_NOT_FOUND     (4008, HttpStatus.NOT_FOUND, "活动订阅不存在"),
    COMPANY_NOT_FOUND                   (4009, HttpStatus.NOT_FOUND, "企业不存在"),
    JOB_INFO_NOT_FOUND                  (4010, HttpStatus.NOT_FOUND, "招聘信息不存在"),
    JOB_SUBSCRIPTION_NOT_FOUND          (4011, HttpStatus.NOT_FOUND, "招聘订阅不存在"),
    USER_NOT_FOUND                      (4012, HttpStatus.NOT_FOUND, "用户不存在"),

    // Unknown Errors.
    UNKNOWN_ERROR(9999, HttpStatus.INTERNAL_SERVER_ERROR, "未知错误");

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
               ", httpStatus=" + httpStatus.value() +
               ", message=" + ToStringUtil.wrap(message) +
               '}';
    }
}
