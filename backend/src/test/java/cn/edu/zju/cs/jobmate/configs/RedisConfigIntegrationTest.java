package cn.edu.zju.cs.jobmate.configs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RedisConfigIntegrationTest {

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
        if (result instanceof Demo) {
            Demo retrievedDemo = (Demo) result;
            assertThat(retrievedDemo.getName()).isEqualTo(demo.getName());
            assertThat(retrievedDemo.getAge()).isEqualTo(demo.getAge());
        } else if (result instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> map = (java.util.Map<String, Object>) result;
            assertThat(map.get("name")).isEqualTo(demo.getName());
            assertThat(map.get("age")).isEqualTo(demo.getAge());
        } else {
            throw new AssertionError("Unexpected result type");
        }
    }
}