package cn.edu.zju.cs.jobmate.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DummyControllerUtil.class)
@AutoConfigureMockMvc(addFilters = false)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

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