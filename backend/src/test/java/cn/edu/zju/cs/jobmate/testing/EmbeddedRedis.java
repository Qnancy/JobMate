package cn.edu.zju.cs.jobmate.testing;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import redis.embedded.RedisServer;

/**
 * Embedded Redis for testing purposes.
 */
@TestConfiguration
public class EmbeddedRedis {

    @Value("${spring.data.redis.port}")
    private int port;

    private RedisServer server;

    @PostConstruct
    public void startRedis() throws IOException {
        server = new RedisServer(port);
        server.start();
    }

    @PreDestroy
    public void stopRedis() {
        if (server != null) {
            server.stop();
        }
    }
}
