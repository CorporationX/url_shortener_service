package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
public class IntegrationTests {

    private static final String URL_KEY_PREFIX = "url:";
    private static final String COUNTER_PREFIX = "counter:";

    @Container
    static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:13.2")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    @Container
    static final GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>("redis:6.2")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
    }

    @Autowired
    private UrlService urlService;

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @Test
    void testCreateAndRetrieveUrl() {
        String originalUrl = "https://example.com";

        String shortUrl = urlService.createShortUrl(originalUrl);

        assertThat(urlRepository.findByHash(shortUrl)).isPresent();

        String retrievedUrl = urlService.getOriginalUrl(shortUrl);
        assertThat(retrievedUrl).isEqualTo(originalUrl);
    }

    @Test
    void testCachingBehavior() {
        long testsCount = 15;
        String originalUrl = "https://example.com/caching";
        String shortUrl = urlService.createShortUrl(originalUrl);

        String notCachedYet = redisTemplate.opsForValue().get(URL_KEY_PREFIX + shortUrl);
        assertThat(notCachedYet).isNull();

        for (int i = 0; i < testsCount; i++) {
            urlService.getOriginalUrl(shortUrl);
        }

        String cachedUrl = redisTemplate.opsForValue().get(URL_KEY_PREFIX + shortUrl);
        assertThat(cachedUrl).isEqualTo(originalUrl);

        String counterValue = redisTemplate.opsForValue().get(COUNTER_PREFIX + shortUrl);
        assertThat(counterValue).isNotNull();
        assertThat(Integer.parseInt(counterValue)).isEqualTo(testsCount);
    }

    @Test
    void testNonExistentUrl() {
        String nonExistentHash = "ABCDEFG";

        assertThatThrownBy(() -> urlService.getOriginalUrl(nonExistentHash))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Url not found");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "https://example.com",
            "https://example.com/path",
            "https://example.com/path?param=value",
            "https://example.com/path#fragment"
    })
    void testDifferentUrlFormats(String url) {
        String shortUrl = urlService.createShortUrl(url);

        String retrievedUrl = urlService.getOriginalUrl(shortUrl);
        assertThat(retrievedUrl).isEqualTo(url);
    }

    @Test
    void testMultipleRequestsSameUrl() {
        String originalUrl = "https://example.com/multiple";
        
        String shortUrl1 = urlService.createShortUrl(originalUrl);
        String shortUrl2 = urlService.createShortUrl(originalUrl);

        assertThat(shortUrl1).isNotEqualTo(shortUrl2);
        
        assertThat(urlService.getOriginalUrl(shortUrl1)).isEqualTo(originalUrl);
        assertThat(urlService.getOriginalUrl(shortUrl2)).isEqualTo(originalUrl);
    }
} 