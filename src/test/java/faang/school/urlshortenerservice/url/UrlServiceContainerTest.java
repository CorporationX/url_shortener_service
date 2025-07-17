package faang.school.urlshortenerservice.url;

import com.redis.testcontainers.RedisContainer;
import faang.school.urlshortenerservice.controller.UrlController;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import faang.school.urlshortenerservice.util.cache.HashCache;
import faang.school.urlshortenerservice.util.cache.UrlRedisCache;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
public class UrlServiceContainerTest {
    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13.6");
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

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    @Autowired
    private HashCache hashCache;
    @Autowired
    private UrlRedisCache redisCache;
    @Autowired
    private UrlRepository urlRepository;
    @Autowired
    private UrlController controller;
    @Autowired
    private Base62Encoder encoder;

    private static final String url = "https://www.pirojok.com/java";

    @Test
    void createTest() {
        UrlDto urlDto = new UrlDto(url);
        String hash = encoder.encode(1);
        assertEquals(hash, controller.createHash(urlDto));
    }

    @Test
    public void findRedisTest() {
        UrlDto urlDto = new UrlDto(url);
        String hash = controller.createHash(urlDto);
        assertEquals(ResponseEntity.status(302).body(url), controller.getUrl(hash));
    }
}
