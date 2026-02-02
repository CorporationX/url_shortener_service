package faang.school.urlshortenerservice.repository;

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Optional;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataRedisTest
@Import(UrlCacheRepositoryImpl.class)
@Testcontainers
@ActiveProfiles("test")
public class UrlCacheRepositoryImplIT {

    private static final String HASH = "G9";

    private static final String URL = "https://ru.pinterest.com/pin/971792425958539876/sent/?invite_code=" +
            "35e8764484c040d8ada204c324f2a9c5&sender=971792563252027821&sfo=1";

    private static final String PREFIX = "url:";

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    UrlCacheRepositoryImpl repository;

    @Container
    private static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis/redis-stack:latest"));

    @DynamicPropertySource
    public static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
    }

    @Test
    void savesHashAndUrl() {
        repository.save(HASH, URL);

        assertEquals(URL, redisTemplate.opsForValue().get(key()));
        assertTrue(redisTemplate.hasKey(key()));
    }

    @Test
    void returnsSavedUrl() {
        redisTemplate.opsForValue().set(key(), URL);

        Optional<String> result = repository.getUrl(HASH);

        assertTrue(result.isPresent());
        assertEquals(URL, result.get());
    }

    @Test
    void entryExpiresAfterTtl() {
        Duration secondsToWait = Duration.ofSeconds(2);

        repository.save(HASH, URL);

        assertTrue(repository.getUrl(HASH).isPresent());

        await()
                .atMost(secondsToWait)
                .untilAsserted(() ->
                        assertTrue(repository.getUrl(HASH).isEmpty())
                );
    }

    @Test
    void doesNotCollideWithoutPrefix() {
        redisTemplate.opsForValue().set(HASH, URL);

        assertTrue(repository.getUrl(HASH).isEmpty());
    }

    private String key() {
        return String.format("%s%s", PREFIX, HASH);
    }
}