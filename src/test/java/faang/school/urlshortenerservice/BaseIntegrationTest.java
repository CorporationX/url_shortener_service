package faang.school.urlshortenerservice;

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;


@Testcontainers
public abstract class BaseIntegrationTest {

    private static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.3")
                    .withDatabaseName("postgres")
                    .withUsername("user")
                    .withPassword("password");
    private static final DockerImageName REDIS_IMAGE = DockerImageName.parse("redis:7.4.2");
    public static final RedisContainer REDIS_CONTAINER = new RedisContainer(REDIS_IMAGE);
    @LocalServerPort
    protected int port;
    protected WebTestClient webTestClient;

    @BeforeAll
    static void startContainer() {
        POSTGRES_CONTAINER.start();
        REDIS_CONTAINER.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
        registry.add("spring.jpa.properties.hibernate.jdbc.batch_size", () -> 250);
        registry.add("hash-generator.generate-count", () -> 1000);
        registry.add("hash-generator.local-cache-size", () -> 100);
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", REDIS_CONTAINER::getFirstMappedPort);
        registry.add("spring.data.redis.url-ttl-in-hours", () -> 12L);
        registry.add("url.short-header", () -> "http://localhost:8080/shortner");
        registry.add("url.ttl-in-mounts", () -> 6L);

    }

    @BeforeEach
    void setUpWebClient() {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
//                .defaultHeader("x-user-id", "1")
                .build();
    }
}
