package cn.edu.zju.cs.jobmate.configs.properties;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WebMvcPropertiesTest {

    @Autowired
    private WebMvcProperties webMvcProperties;

    @Test
    void testCorsPropertiesBinding() {
        assertNotNull(webMvcProperties.getCors(), "CORS config should not be null");
        assertNotNull(webMvcProperties.getCors().getAllowedOrigins(), "Allowed origins should not be null");
        assertEquals(1, webMvcProperties.getCors().getAllowedOrigins().length);
        assertEquals("https://localhost:3000", webMvcProperties.getCors().getAllowedOrigins()[0]);
        assertEquals(3600L, webMvcProperties.getCors().getMaxAge());
    }
}