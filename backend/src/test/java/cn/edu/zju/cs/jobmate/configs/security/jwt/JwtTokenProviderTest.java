package cn.edu.zju.cs.jobmate.configs.security.jwt;

import cn.edu.zju.cs.jobmate.configs.properties.JwtProperties;
import cn.edu.zju.cs.jobmate.models.User;
import cn.edu.zju.cs.jobmate.security.jwt.JwtTokenProvider;
import cn.edu.zju.cs.jobmate.enums.UserRole;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider provider;
    private JwtProperties properties;

    @BeforeEach
    void setUp() {
        properties = new JwtProperties();
        properties.setSecret("Th15!5@S3cr3tK3yF0rJ06M4t3@99l!(@t10n123456");
        properties.setExpiration(3600_000L); // 1 hour.

        provider = new JwtTokenProvider();
        ReflectionTestUtils.setField(provider, "properties", properties);
    }

    @Test
    void testGenerateAndParseToken() throws Exception {
        User user = new User("testuser", "password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 42);

        String token = provider.generateToken(user);
        assertNotNull(token);

        assertTrue(provider.validateToken(token));
        assertEquals("testuser", provider.getUsernameFromToken(token));
    }

    @Test
    void testValidateToken_expired() throws Exception {
        properties.setExpiration(1L); // 1 ms.
        User user = new User("expireduser", "password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 99);

        String token = provider.generateToken(user);
        Thread.sleep(5); // Wait for token to expire.
        assertFalse(provider.validateToken(token));
    }

    @Test
    void testValidateToken_invalidSignature() throws Exception {
        User user = new User("user", "password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1);

        String token = provider.generateToken(user);

        // Change the secret key to invalidate the token signature.
        properties.setSecret("AnotherSecretKeyThatIsLongEnough1234567890");
        assertFalse(provider.validateToken(token));
    }
}
