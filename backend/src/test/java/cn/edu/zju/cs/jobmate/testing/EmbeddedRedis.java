package cn.edu.zju.cs.jobmate.testing;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import redis.embedded.RedisServer;

/**
 * Embedded Redis for testing purposes.
 * 
 * @apiNote Manually manage the lifecycle of the embedded Redis server
 *         ({@link #start()} and {@link #stop()})
 *         in your test setup(using {@link BeforeAll}) and teardown(using {@link AfterAll})
 *         so that it does not interfere with other tests.
 */
public class EmbeddedRedis {

    private static final int port = 6378;

    private RedisServer server;

    /**
     * Start the embedded Redis server.
     */
    public void start() {
        server = new RedisServer(port);
        server.start();
    }

    /**
     * Stop the embedded Redis server.
     */
    public void stop() {
        if (server != null) {
            server.stop();
        }
    }
}
