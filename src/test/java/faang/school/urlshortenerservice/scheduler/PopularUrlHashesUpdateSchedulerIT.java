package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.BaseContextIT;
import faang.school.urlshortenerservice.properties.short_url.UrlCacheProperties;
import faang.school.urlshortenerservice.scheduler.popular_hash.PopularUrlHashesUpdateScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Sql("/db/test_sql/insert_url_records.sql")
public class PopularUrlHashesUpdateSchedulerIT extends BaseContextIT {

    @Autowired
    protected RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UrlCacheProperties urlCacheProperties;

    @Autowired
    private PopularUrlHashesUpdateScheduler scheduler;

    @BeforeEach
    void setUp() {
        redisTemplate.opsForZSet().incrementScore(urlCacheProperties.getPopularCacheName(), "hash1",1);
        redisTemplate.opsForZSet().incrementScore(urlCacheProperties.getPopularCacheName(), "hash2",1);
    }

    @Test
    void updatePopularShortUrlsTest() throws InterruptedException {
        assertEquals(2, redisTemplate.opsForZSet().size(urlCacheProperties.getPopularCacheName()));
        scheduler.updatePopularShortUrls();
        Thread.sleep(2000);

        Awaitility.await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertEquals(0, redisTemplate.opsForZSet().size(urlCacheProperties.getPopularCacheName()));
                    assertTrue(redisTemplate.getExpire("%s::%s".formatted(urlCacheProperties.getDefaultCacheName(), "hash1"))
                            > urlCacheProperties.getDefaultTtlMinutes() * 60L);
                    assertTrue(redisTemplate.getExpire("%s::%s".formatted(urlCacheProperties.getDefaultCacheName(), "hash2"))
                            > urlCacheProperties.getDefaultTtlMinutes() * 60L);
                });
    }
}
