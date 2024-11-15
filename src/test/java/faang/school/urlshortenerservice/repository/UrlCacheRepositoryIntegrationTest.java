package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.entity.RedisCachedUrl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.autoconfigure.data.redis.AutoConfigureDataRedis;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DataRedisTest
@AutoConfigureDataRedis
public class UrlCacheRepositoryIntegrationTest {

    @Autowired
    private UrlCacheRepository urlCacheRepository;

    @Test
    public void testSaveAndRetrieveUrlCache() {
        RedisCachedUrl cachedUrl = new RedisCachedUrl();
        cachedUrl.setId("testId");
        cachedUrl.setLongUrl("https://example.com/long-url");
        cachedUrl.setCreatedAt(LocalDateTime.now());

        urlCacheRepository.save(cachedUrl);

        Optional<RedisCachedUrl> retrieved = urlCacheRepository.findById("testId");

        assertTrue(retrieved.isPresent());
        assertEquals("https://example.com/long-url", retrieved.get().getLongUrl());
        assertEquals("testId", retrieved.get().getId());
    }

    @Test
    public void testDeleteUrlCache() {
        RedisCachedUrl cachedUrl = new RedisCachedUrl();
        cachedUrl.setId("testDeleteId");
        cachedUrl.setLongUrl("https://example.com/delete-url");
        cachedUrl.setCreatedAt(LocalDateTime.now());
        urlCacheRepository.save(cachedUrl);

        urlCacheRepository.deleteById("testDeleteId");

        Optional<RedisCachedUrl> deleted = urlCacheRepository.findById("testDeleteId");
        assertFalse(deleted.isPresent());
    }

    @Test
    public void testUpdateUrlCache() {
        RedisCachedUrl cachedUrl = new RedisCachedUrl();
        cachedUrl.setId("testUpdateId");
        cachedUrl.setLongUrl("https://example.com/initial-url");
        cachedUrl.setCreatedAt(LocalDateTime.now());
        urlCacheRepository.save(cachedUrl);

        cachedUrl.setLongUrl("https://example.com/updated-url");
        urlCacheRepository.save(cachedUrl);

        Optional<RedisCachedUrl> updated = urlCacheRepository.findById("testUpdateId");

        assertTrue(updated.isPresent());
        assertEquals("https://example.com/updated-url", updated.get().getLongUrl());
    }

    @Test
    public void testBatchSaveAndRetrieveUrlCache() {
        RedisCachedUrl cachedUrl1 = new RedisCachedUrl();
        cachedUrl1.setId("testBatchId1");
        cachedUrl1.setLongUrl("https://example.com/url1");
        cachedUrl1.setCreatedAt(LocalDateTime.now());

        RedisCachedUrl cachedUrl2 = new RedisCachedUrl();
        cachedUrl2.setId("testBatchId2");
        cachedUrl2.setLongUrl("https://example.com/url2");
        cachedUrl2.setCreatedAt(LocalDateTime.now());

        urlCacheRepository.saveAll(List.of(cachedUrl1, cachedUrl2));

        Optional<RedisCachedUrl> retrieved1 = urlCacheRepository.findById("testBatchId1");
        Optional<RedisCachedUrl> retrieved2 = urlCacheRepository.findById("testBatchId2");

        assertTrue(retrieved1.isPresent());
        assertTrue(retrieved2.isPresent());
        assertEquals("https://example.com/url1", retrieved1.get().getLongUrl());
        assertEquals("https://example.com/url2", retrieved2.get().getLongUrl());
    }
}
