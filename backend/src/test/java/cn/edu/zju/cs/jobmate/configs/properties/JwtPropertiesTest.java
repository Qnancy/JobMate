package cn.edu.zju.cs.jobmate.configs.properties;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JwtPropertiesTest {

    @Autowired
    private JwtProperties properties;

    @Test
    void testJwtPropertiesBinding() {
        assertEquals(properties.getSecret(), "ThisIsATestSecretKeyForJobMateApplication");
        assertEquals(properties.getExpiration(), 86400000L);
    }
}
