package faang.school.urlshortenerservice.service;

import com.redis.testcontainers.RedisContainer;
import faang.school.urlshortenerservice.component.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
@Transactional
@Slf4j
@ActiveProfiles("test")
public class UrlServiceIT {
    @Autowired
    private UrlService urlService;
    @Autowired
    private UrlRepository urlRepository;
    @Autowired
    private UrlCacheRepository urlCacheRepository;
    @Autowired
    private HashCache hashCache;


    private UrlDto urlDto;

    @BeforeEach
    public void setUp() {
        urlDto = new UrlDto("https://faang-school");
        hashCache.init();
    }

    @Test
    public void testPositiveShortenUrl() {
        String hash = urlService.shortenUrl(urlDto);
        List<String> listUrl = urlRepository.findAllUrls();
        Set<String> listHash = urlCacheRepository.findAllHashes();
        assertEquals(1, listUrl.size());
        assertEquals(hash, listUrl.get(0));
        assertEquals(1, listHash.size());
        assertEquals(urlDto.url(), urlCacheRepository.findByHash(hash).get());
        assertEquals(19, hashCache.getHashes().size());
    }

    @Test
    public void testPositiveOriginalUrl() {
        String hash = urlService.shortenUrl(urlDto);
        String originalUrl = urlService.getOriginalUrl(hash);
        assertEquals(urlDto.url(), originalUrl);
    }

    @Test
    public void testNegativeShortenUrl() {
        urlService.shortenUrl(urlDto);
        assertThrows(EntityNotFoundException.class,
                () -> urlService.getOriginalUrl("123"));
    }

    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.6");

    @Container
    public static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis/redis-stack:latest"));


    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));

        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
