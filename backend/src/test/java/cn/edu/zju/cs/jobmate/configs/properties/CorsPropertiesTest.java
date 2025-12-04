package cn.edu.zju.cs.jobmate.configs.properties;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CorsPropertiesTest {

    @Autowired
    private CorsProperties properties;

    @Test
    void testCorsPropertiesBinding() {
        assertEquals(1, properties.getAllowedOrigins().size());
        assertEquals("https://localhost:3000", properties.getAllowedOrigins().get(0));
        assertEquals(3600L, properties.getMaxAge());
    }
}
