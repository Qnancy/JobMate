package cn.edu.zju.cs.jobmate.exceptions;

import cn.edu.zju.cs.jobmate.dto.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.validation.ConstraintViolationException;

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
     * Handle Spring @Valid validation failures.
     * 
     * @param ex the method argument not valid exception
     * @return a standardized error response with 400 status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(ErrorCode.INVALID_PARAMETER));
    }

    /**
     * Handle Jakarta @Validated constraint violations.
     * 
     * @param ex the constraint violation exception
     * @return a standardized error response with 400 status
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(ConstraintViolationException ex) {
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
        log.error("Uncaught exception occurred: ", ex);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(500, "服务器内部错误，请联系管理员"));
    }
}
