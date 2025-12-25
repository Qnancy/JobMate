package cn.edu.zju.cs.jobmate.configs.properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AdminPropertiesTest {

    @Autowired
    private AdminProperties properties;

    @Test
    void testSecretPropertiesBinding() {
        assertEquals("ADMIN", properties.getSecret());
    }
}
