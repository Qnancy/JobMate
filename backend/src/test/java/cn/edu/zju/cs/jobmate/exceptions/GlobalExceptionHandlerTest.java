package cn.edu.zju.cs.jobmate.exceptions;

import cn.edu.zju.cs.jobmate.dto.common.ApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import jakarta.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("GlobalExceptionHandler异常处理测试")
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    private GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    @DisplayName("测试BusinessException处理 - USER_NOT_FOUND")
    void testHandleBusinessException_UserNotFound() {
        // 创建BusinessException
        BusinessException exception = new BusinessException(ErrorCode.USER_NOT_FOUND);
        
        // 调用处理器
        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler.handleBusinessException(exception);
        
        // 验证响应
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getCode());
        assertEquals("用户不存在", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    @DisplayName("测试BusinessException处理 - COMPANY_NOT_FOUND")
    void testHandleBusinessException_CompanyNotFound() {
        BusinessException exception = new BusinessException(ErrorCode.COMPANY_NOT_FOUND);
        
        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler.handleBusinessException(exception);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getCode());
        assertEquals("公司不存在", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    @DisplayName("测试BusinessException处理 - JOB_INFO_NOT_FOUND")
    void testHandleBusinessException_JobInfoNotFound() {
        BusinessException exception = new BusinessException(ErrorCode.JOB_INFO_NOT_FOUND);
        
        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler.handleBusinessException(exception);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getCode());
        assertEquals("招聘信息不存在", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    @DisplayName("测试BusinessException处理 - ACTIVITY_INFO_NOT_FOUND")
    void testHandleBusinessException_ActivityInfoNotFound() {
        BusinessException exception = new BusinessException(ErrorCode.ACTIVITY_INFO_NOT_FOUND);
        
        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler.handleBusinessException(exception);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getCode());
        assertEquals("活动信息不存在", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    @DisplayName("测试BusinessException处理 - COMPANY_ALREADY_EXISTS")
    void testHandleBusinessException_CompanyAlreadyExists() {
        BusinessException exception = new BusinessException(ErrorCode.COMPANY_ALREADY_EXISTS);
        
        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler.handleBusinessException(exception);
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().getCode());
        assertEquals("公司已存在", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    @DisplayName("测试BusinessException处理 - JOB_SUBSCRIPTION_NOT_FOUND")
    void testHandleBusinessException_JobSubscriptionNotFound() {
        BusinessException exception = new BusinessException(ErrorCode.JOB_SUBSCRIPTION_NOT_FOUND);
        
        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler.handleBusinessException(exception);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getCode());
        assertEquals("招聘订阅不存在", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    @DisplayName("测试BusinessException处理 - ACTIVITY_SUBSCRIPTION_ALREADY_EXISTS")
    void testHandleBusinessException_ActivitySubscriptionAlreadyExists() {
        BusinessException exception = new BusinessException(ErrorCode.ACTIVITY_SUBSCRIPTION_ALREADY_EXISTS);
        
        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler.handleBusinessException(exception);
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().getCode());
        assertEquals("活动订阅已存在", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    @DisplayName("测试MethodArgumentNotValidException处理")
    void testHandleValidationException() {
        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler.handleValidationException(null);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getCode());
        assertEquals("参数无效", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    @DisplayName("测试ConstraintViolationException处理")
    void testHandleConstraintViolationException() {
        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler.handleConstraintViolationException(null);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getCode());
        assertEquals("参数无效", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    @DisplayName("测试未捕获异常处理")
    void testHandleUncaughtException() {
        Exception exception = new RuntimeException("测试异常");
        
        ResponseEntity<ApiResponse<Void>> response = globalExceptionHandler.handleUncaughtException(exception);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getCode());
        assertEquals("服务器内部错误，请联系管理员", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    @DisplayName("测试HTTP状态码与ErrorCode映射正确性")
    void testErrorCodeHttpStatusMapping() {
        // 测试NOT_FOUND错误码都映射到404
        assertEquals(404, ErrorCode.USER_NOT_FOUND.getHttpStatus().value());
        assertEquals(404, ErrorCode.COMPANY_NOT_FOUND.getHttpStatus().value());
        assertEquals(404, ErrorCode.JOB_INFO_NOT_FOUND.getHttpStatus().value());
        assertEquals(404, ErrorCode.ACTIVITY_INFO_NOT_FOUND.getHttpStatus().value());
        assertEquals(404, ErrorCode.JOB_SUBSCRIPTION_NOT_FOUND.getHttpStatus().value());
        assertEquals(404, ErrorCode.ACTIVITY_SUBSCRIPTION_NOT_FOUND.getHttpStatus().value());
        
        // 测试CONFLICT错误码都映射到409
        assertEquals(409, ErrorCode.COMPANY_ALREADY_EXISTS.getHttpStatus().value());
        assertEquals(409, ErrorCode.JOB_SUBSCRIPTION_ALREADY_EXISTS.getHttpStatus().value());
        assertEquals(409, ErrorCode.ACTIVITY_SUBSCRIPTION_ALREADY_EXISTS.getHttpStatus().value());
        
        // 测试BAD_REQUEST错误码映射到400
        assertEquals(400, ErrorCode.INVALID_PARAMETER.getHttpStatus().value());
        assertEquals(400, ErrorCode.MISSING_REQUIRED_PARAMETER.getHttpStatus().value());
        
        // 测试INTERNAL_SERVER_ERROR错误码映射到500
        assertEquals(500, ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus().value());
        assertEquals(500, ErrorCode.UNKNOWN_ERROR.getHttpStatus().value());
    }

    @Test
    @DisplayName("测试错误消息内容正确性")
    void testErrorCodeMessageContent() {
        // 验证错误消息内容
        assertEquals("用户不存在", ErrorCode.USER_NOT_FOUND.getMessage());
        assertEquals("公司不存在", ErrorCode.COMPANY_NOT_FOUND.getMessage());
        assertEquals("招聘信息不存在", ErrorCode.JOB_INFO_NOT_FOUND.getMessage());
        assertEquals("活动信息不存在", ErrorCode.ACTIVITY_INFO_NOT_FOUND.getMessage());
        assertEquals("招聘订阅不存在", ErrorCode.JOB_SUBSCRIPTION_NOT_FOUND.getMessage());
        assertEquals("活动订阅不存在", ErrorCode.ACTIVITY_SUBSCRIPTION_NOT_FOUND.getMessage());
        
        assertEquals("公司已存在", ErrorCode.COMPANY_ALREADY_EXISTS.getMessage());
        assertEquals("招聘订阅已存在", ErrorCode.JOB_SUBSCRIPTION_ALREADY_EXISTS.getMessage());
        assertEquals("活动订阅已存在", ErrorCode.ACTIVITY_SUBSCRIPTION_ALREADY_EXISTS.getMessage());
        
        assertEquals("参数无效", ErrorCode.INVALID_PARAMETER.getMessage());
        assertEquals("缺少必需参数", ErrorCode.MISSING_REQUIRED_PARAMETER.getMessage());
        assertEquals("服务器内部错误", ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
    }
}