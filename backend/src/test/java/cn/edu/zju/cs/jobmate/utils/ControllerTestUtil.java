package cn.edu.zju.cs.jobmate.utils;

import static org.mockito.ArgumentMatchers.anyString;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Base controller test utility class.
 */
public abstract class ControllerTestUtil {

    @MockitoBean
    protected StringRedisTemplate redis;

    @BeforeEach
    @SuppressWarnings({"null", "unchecked"})
    void setUp() {
        ValueOperations<String, String> ops = Mockito.mock(ValueOperations.class);
        Mockito.when(redis.opsForValue()).thenReturn(ops);
        Mockito.when(ops.increment(anyString())).thenReturn(0L);
    }
}
