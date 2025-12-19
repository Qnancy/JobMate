package cn.edu.zju.cs.jobmate.configs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import cn.edu.zju.cs.jobmate.configs.properties.MonitorProperties;
import cn.edu.zju.cs.jobmate.testing.DummyController;

import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DummyController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(value = MonitorProperties.class)
@TestPropertySource(properties = {
    "app.monitor.rate-limiter.enabled=true",
    "app.monitor.rate-limiter.limit-interval-s=10",
    "app.monitor.rate-limiter.max-requests-per-interval=5"
})
class WebMvcConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StringRedisTemplate stringRedisTemplate;

    long cnt; // Mock rate limit counter.

    @BeforeEach
    @SuppressWarnings({"null", "unchecked"})
    void setUp() {
        cnt = 0;
        ValueOperations<String, String> valueOps = Mockito.mock(ValueOperations.class);
        Mockito.when(stringRedisTemplate.opsForValue()).thenReturn(valueOps);
        Mockito.when(valueOps.increment(anyString())).thenAnswer(invocation -> ++cnt);
    }

    @Test
    void testRateLimitInterceptor() throws Exception {
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/api/test")).andExpect(status().isOk());
        }
        mockMvc.perform(get("/api/test")).andExpect(status().isTooManyRequests());
    }
}
