package cn.edu.zju.cs.jobmate.configs.properties;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MonitorPropertiesTest {

    @Autowired
    private MonitorProperties monitorProperties;

    @Test
    void testSlowApiConfigBinding() {
        MonitorProperties.SlowApi slowApi = monitorProperties.getSlowApi();
        assertNotNull(slowApi, "SlowApi config should not be null");
        assertTrue(slowApi.isEnabled());
        assertEquals(1000L, slowApi.getThresholdMs());
    }

    @Test
    void testRateLimiterConfigBinding() {
        MonitorProperties.RateLimiter rateLimiter = monitorProperties.getRateLimiter();
        assertNotNull(rateLimiter, "RateLimiter config should not be null");
        assertTrue(rateLimiter.isEnabled());
        assertEquals(10, rateLimiter.getLimitIntervalS());
        assertEquals(5, rateLimiter.getMaxRequestsPerInterval());
    }
}
