package cn.edu.zju.cs.jobmate.exceptions;

import cn.edu.zju.cs.jobmate.dto.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;

/**
 * Global exception handler for the application.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle {@link BusinessException}.
     * 
     * @param ex the business exception
     * @return a standardized error response
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
        ErrorCode code = ex.getErrorCode();
        log.info("Captured BusinessException: {}", code.toString());
        return ResponseEntity
            .status(Objects.requireNonNull(code.getHttpStatus()))
            .body(ApiResponse.error(code));
    }

    /**
     * Handle {@link AccessDeniedException}.
     * 
     * @param ex the access denied exception
     * @return a standardized error response with 403 status
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        log.info("Access denied");
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error(ErrorCode.FORBIDDEN_ACCESS));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        log.warn("No handler for {} {}", ex.getHttpMethod(), ex.getRequestURL());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ErrorCode.RESOURCE_NOT_FOUND));
    }

    /**
     * Handle {@link Valid} validation failures.
     * 
     * @param ex the Spring {@link MethodArgumentNotValidException}
     * @return a standardized error response with 400 status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("Invalid method argument: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(ErrorCode.INVALID_PARAMETER));
    }

    /**
     * Handle Spring {@link Validated} constraint violations.
     * 
     * @param ex the {@link ConstraintViolationException}
     * @return a standardized error response with 400 status
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("Constraint violation: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(ErrorCode.INVALID_PARAMETER));
    }

    /**
     * Handle all uncaught exceptions.
     * 
     * @param ex the uncaught exception
     * @return a standardized error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUncaughtException(Exception ex) {
        log.error("Uncaught exception occurred: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(500, "服务器内部错误，请联系管理员"));
    }
}
