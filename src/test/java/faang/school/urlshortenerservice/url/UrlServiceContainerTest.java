package faang.school.urlshortenerservice.url;

import com.redis.testcontainers.RedisContainer;
import faang.school.urlshortenerservice.controller.UrlController;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.util.CleanerScheduler;
import faang.school.urlshortenerservice.util.cache.UrlRedisCache;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;


import java.util.List;

import jakarta.validation.Validator;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
public class UrlServiceContainerTest {

    @Container
    public static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:13.6");
    @Container
    private static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis/redis-stack:latest"));

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        postgres.start();
        REDIS_CONTAINER.start();
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
    }

    @Autowired
    private UrlController controller;
    @Autowired
    private UrlRedisCache redisCache;
    @Autowired
    private CleanerScheduler cleaner;
    @Autowired
    private Validator validator;

    @Value("${url.base-url}")
    private String baseUrl;

    private static final String url = "https://www.barnay.com/java";

    @Test
    void createTest() {
        UrlDto urlDto = new UrlDto(url);
        String result = controller.createHash(urlDto);
        assertTrue(result.startsWith(baseUrl));

        String hash = result.replace(baseUrl, "");
        ResponseEntity<Object> response = controller.getUrl(hash);
        assertEquals(302, response.getStatusCodeValue());
        assertEquals(url, response.getHeaders().getFirst("Location"));
    }

    @Test
    void wrongDto() {
        UrlDto urlDto = new UrlDto("23");
        var violations = validator.validate(urlDto);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void findRedisTest() {
        UrlDto urlDto = new UrlDto(url);
        String urlHash = controller.createHash(urlDto);
        String hash = urlHash.replace(baseUrl, "");

        ResponseEntity<Object> response = controller.getUrl(hash);
        assertEquals(302, response.getStatusCodeValue());
        assertEquals(url, response.getHeaders().getFirst("Location"));
    }

    @Test
    public void findDBTest() {
        UrlDto urlDto = new UrlDto(url);
        String urlHash = controller.createHash(urlDto);
        String hash = urlHash.replace(baseUrl, "");
        redisCache.delete(hash);

        ResponseEntity<Object> response = controller.getUrl(hash);
        assertEquals(302, response.getStatusCodeValue());
        assertEquals(url, response.getHeaders().getFirst("Location"));
    }

    @Test
    public void scheduledMethodTest() {
        List<?> result = cleaner.clean();
        assertNotNull(result);
        assertTrue(result instanceof List);
    }
}
