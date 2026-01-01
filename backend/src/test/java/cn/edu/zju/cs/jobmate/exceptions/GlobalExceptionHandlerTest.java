package cn.edu.zju.cs.jobmate.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import cn.edu.zju.cs.jobmate.configs.properties.MonitorProperties;
import cn.edu.zju.cs.jobmate.testing.DummyController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DummyController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(value = MonitorProperties.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void testHandleBusinessException() throws Exception {
        mockMvc.perform(get("/api/test/business-ex"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.code").value(ErrorCode.UNKNOWN_ERROR.getHttpStatus().value()))
            .andExpect(jsonPath("$.message").value(ErrorCode.UNKNOWN_ERROR.getMessage()))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testHandleAccessDeniedException() throws Exception {
        // See in {@link AuthenticationTest#testNonAdminAccessAdminEndpoint_Forbidden()}.
    }

    @Test
    void testHandleNoHandlerFoundException() throws Exception {
        mockMvc.perform(get("/api/non-existent-endpoint"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value(ErrorCode.RESOURCE_NOT_FOUND.getHttpStatus().value()))
            .andExpect(jsonPath("$.message").value(ErrorCode.RESOURCE_NOT_FOUND.getMessage()))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testHandleMethodArgumentNotValidException() throws Exception {
        mockMvc.perform(post("/api/test/valid")
            .contentType("application/json")
            .content("{}")) // Missing required fields.
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_PARAMETER.getHttpStatus().value()))
            .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_PARAMETER.getMessage()))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testHandleConstraintViolationException() throws Exception {
        mockMvc.perform(get("/api/test/constraint/-1")) // Invalid parameter.
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_PARAMETER.getHttpStatus().value()))
            .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_PARAMETER.getMessage()))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testHandleUncaughtException() throws Exception {
        mockMvc.perform(get("/api/test/uncaught-ex"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.code").value(500))
            .andExpect(jsonPath("$.message").value("服务器内部错误，请联系管理员"))
            .andExpect(jsonPath("$.data").isEmpty());
    }
}
