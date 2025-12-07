package cn.edu.zju.cs.jobmate.configs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import cn.edu.zju.cs.jobmate.utils.EmbeddedRedisConfigUtil;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {"spring.data.redis.port=6378"})
@Import(EmbeddedRedisConfigUtil.class)
class RedisConfigTest {

    static class Demo {
        private String name;
        private int age;

        public Demo() {}
        public Demo(String name, int age) { this.name = name; this.age = age; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
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
        if (retrievedDemo == null) {
            throw new AssertionError("Retrieved Demo object is null");
        }
        assertThat(retrievedDemo.getName()).isEqualTo(demo.getName());
        assertThat(retrievedDemo.getAge()).isEqualTo(demo.getAge());
    }
}
