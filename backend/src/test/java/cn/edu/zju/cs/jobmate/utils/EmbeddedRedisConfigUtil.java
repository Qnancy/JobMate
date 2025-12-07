package cn.edu.zju.cs.jobmate.utils;

import java.io.IOException;

import org.springframework.boot.test.context.TestConfiguration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import redis.embedded.RedisServer;

/**
 * Embedded Redis configuration for testing purposes.
 */
@TestConfiguration
public class EmbeddedRedisConfigUtil {

    private RedisServer server;

    @PostConstruct
    public void startRedis() throws IOException {
        server = new RedisServer(6378);
        server.start();
    }

    @PreDestroy
    public void stopRedis() {
        if (server != null) {
            server.stop();
        }
    }
}
