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
import cn.edu.zju.cs.jobmate.utils.DummyControllerUtil;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DummyControllerUtil.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(value = MonitorProperties.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void testHandleBusinessException() throws Exception {
        mockMvc.perform(get("/business-ex"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.code").value(ErrorCode.UNKNOWN_ERROR.getHttpStatus().value()))
            .andExpect(jsonPath("$.message").value(ErrorCode.UNKNOWN_ERROR.getMessage()))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testHandleUncaughtException() throws Exception {
        mockMvc.perform(get("/uncaught-ex"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.code").value(500))
            .andExpect(jsonPath("$.message").value("服务器内部错误，请联系管理员"))
            .andExpect(jsonPath("$.data").isEmpty());
    }
}
