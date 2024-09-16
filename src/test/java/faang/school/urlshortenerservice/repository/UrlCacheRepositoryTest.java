package faang.school.urlshortenerservice.repository;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class UrlCacheRepositoryTest {

    @Container
    public static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:6.2"))
            .withExposedPorts(6379);

    private UrlCacheRepository urlCacheRepository;
    private StringRedisTemplate redisTemplate;

    @BeforeEach
    void setUp() {
        redis.start();

        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(redis.getHost());
        redisConfig.setPort(redis.getFirstMappedPort());

        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(redisConfig);
        connectionFactory.afterPropertiesSet();

        redisTemplate = new StringRedisTemplate(connectionFactory);
        redisTemplate.afterPropertiesSet();
        urlCacheRepository = new UrlCacheRepository(redisTemplate, 3600);
    }

    @Test
    void testSaveUrlAndGetUrl() {
        String hash = "abc123";
        String url = "https://example.com";

        urlCacheRepository.saveUrl(hash, url);

        String retrievedUrl = urlCacheRepository.getUrl(hash);
        assertEquals(url, retrievedUrl);
    }

    @Test
    void testGetNonExistentUrl() {
        String hash = "nonexistent";

        String retrievedUrl = urlCacheRepository.getUrl(hash);
        assertNull(retrievedUrl);
    }

    @Test
    void testCacheTtl() {
        String hash = "ghi789";
        String url = "https://example.net";

        urlCacheRepository = new UrlCacheRepository(redisTemplate, 1);
        urlCacheRepository.saveUrl(hash, url);

        String retrievedUrl = urlCacheRepository.getUrl(hash);
        assertEquals(url, retrievedUrl);

        Awaitility.await()
                .atMost(3, TimeUnit.SECONDS)
                .pollInterval(Duration.ofMillis(100))
                .until(() -> urlCacheRepository.getUrl(hash) == null);
    }
}