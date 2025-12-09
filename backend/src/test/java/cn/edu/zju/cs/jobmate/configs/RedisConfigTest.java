package cn.edu.zju.cs.jobmate.configs;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import cn.edu.zju.cs.jobmate.utils.EmbeddedRedisConfigUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(value = EmbeddedRedisConfigUtil.class)
class RedisConfigTest {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class Demo {
        private String name;
        private int age;
    }

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void testRedisTemplateWithString() {
        String key = "test:key";
        String value = "hello";
        redisTemplate.opsForValue().set(key, value);
        Object result = redisTemplate.opsForValue().get(key);
        assertThat(result).isEqualTo(value);
    }

    @Test
    void testRedisTemplateWithCustomObject() {
        String key = "test:demo";
        Demo demo = new Demo("Alice", 30);
        redisTemplate.opsForValue().set(key, demo);
        Object result = redisTemplate.opsForValue().get(key);
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(Demo.class);
        Demo retrievedDemo = (Demo) result;
        assertNotNull(retrievedDemo);
        assertThat(retrievedDemo.getName()).isEqualTo(demo.getName());
        assertThat(retrievedDemo.getAge()).isEqualTo(demo.getAge());
    }
}
