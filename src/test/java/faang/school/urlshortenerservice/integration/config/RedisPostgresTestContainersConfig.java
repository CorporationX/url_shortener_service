package faang.school.urlshortenerservice.integration.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class RedisPostgresTestContainersConfig {
    private static final String POSTGRES_IMAGE = "postgres:13.3";
    private static final String POSTGRES_DB_NAME = "testdb";
    private static final String POSTGRES_USER = "testuser";
    private static final String POSTGRES_PASSWORD = "testpassword";

    private static final String REDIS_IMAGE = "redis:latest";
    private static final int REDIS_PORT = 6379;

    @SuppressWarnings("resource")
    @Container
    public static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>(DockerImageName.parse(POSTGRES_IMAGE))
                    .withDatabaseName(POSTGRES_DB_NAME)
                    .withUsername(POSTGRES_USER)
                    .withPassword(POSTGRES_PASSWORD);

    @SuppressWarnings("resource")
    @Container
    public static final GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse(REDIS_IMAGE))
            .withExposedPorts(REDIS_PORT);

    @DynamicPropertySource
    public static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);

        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(REDIS_PORT).toString());
    }
}
