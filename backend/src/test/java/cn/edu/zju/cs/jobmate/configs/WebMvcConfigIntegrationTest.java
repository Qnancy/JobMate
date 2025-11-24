package cn.edu.zju.cs.jobmate.configs;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.beans.factory.annotation.Autowired;
import redis.embedded.RedisServer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WebMvcConfigIntegrationTest {

    private static RedisServer redisServer;

    @BeforeAll
    static void startRedis() throws Exception {
        redisServer = new RedisServer(6378);
        redisServer.start();
    }

    @AfterAll
    static void stopRedis() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testRateLimitInterceptor() throws Exception {
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/api/test"))
                    .andExpect(status().isOk());
        }
        mockMvc.perform(get("/api/test"))
                .andExpect(status().isTooManyRequests());
    }
}
