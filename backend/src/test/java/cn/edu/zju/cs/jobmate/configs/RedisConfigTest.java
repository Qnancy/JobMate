package cn.edu.zju.cs.jobmate.configs;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import redis.embedded.RedisServer;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
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

    private static RedisServer redisServer;

    @BeforeAll
    static void startRedis() throws Exception {
        redisServer = new RedisServer(6379);
        redisServer.start();
    }

    @AfterAll
    static void stopRedis() {
        if (redisServer != null) {
            redisServer.stop();
        }
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
