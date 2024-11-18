package faang.school.urlshortenerservice.util;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class RedisTestContainer {

    private static final GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>("redis:7.0-alpine")
            .withExposedPorts(6379)
            .waitingFor(Wait.forListeningPort());

    static {
        REDIS_CONTAINER.start();
    }

    public static GenericContainer<?> getRedisContainer() {
        return REDIS_CONTAINER;
    }
}
