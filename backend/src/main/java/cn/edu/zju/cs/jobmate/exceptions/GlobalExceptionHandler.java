package cn.edu.zju.cs.jobmate.exceptions;

import cn.edu.zju.cs.jobmate.dto.common.ApiResponse;

import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for the application.
 * 
 * TODO: Add logging functionality.
 */
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
        return ResponseEntity
            .status(Objects.requireNonNull(code.getHttpStatus()))
            .body(ApiResponse.error(code));
    }

    /**
     * Handle all uncaught exceptions.
     * 
     * @param ex the uncaught exception
     * @return a standardized error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUncaughtException(Exception ex) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(500, "服务器内部错误，请联系管理员"));
    }
}
