package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.local.cache.CacheInitializer;
import faang.school.urlshortenerservice.local.cache.LocalCache;
import faang.school.urlshortenerservice.model.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.util.Base64Encoder;
import faang.school.urlshortenerservice.util.RedisAndPostgresSQLContainerConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.TimeUnit;

@SpringBootTest
class UrlShortenerIntegrationTest extends RedisAndPostgresSQLContainerConfig {

    @Autowired
    private CacheInitializer cacheInitializer;

    @Autowired
    private LocalCache localCache;

    @Autowired
    private HashGenerator hashGenerator;

    @Autowired
    private Base64Encoder base64Encoder;

    @Autowired
    private HashRepository hashRepository;

    @Autowired
    @Qualifier("hashAsyncExecutor")
    private ThreadPoolTaskExecutor hashAsyncExecutor;

    @Autowired
    @Qualifier("base64EncodingExecutor")
    private ThreadPoolTaskExecutor base64EncodingExecutor;

    @Autowired
    @Qualifier("hashGeneratorExecutor")
    private ThreadPoolTaskExecutor hashGeneratorExecutor;

    @Autowired
    private UrlService urlService;

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private UrlCacheRepository urlCacheRepository;

    @Autowired
    private EntityManager entityManager;

    @AfterEach
    void waitForAsyncTasks() {
        shutdownAndAwaitTermination(hashAsyncExecutor);
        shutdownAndAwaitTermination(base64EncodingExecutor);
        shutdownAndAwaitTermination(hashGeneratorExecutor);
    }

    private void shutdownAndAwaitTermination(ThreadPoolTaskExecutor executor) {
        executor.shutdown();
        try {
            if (!executor.getThreadPoolExecutor().awaitTermination(60, TimeUnit.SECONDS)) {
                executor.getThreadPoolExecutor().shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.getThreadPoolExecutor().shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testInitCacheCreated() {
        Assertions.assertTrue(localCache.isCacheHasHashes());
    }

    @Test
    void testCreateShortUrl() {
        String originalUrl = "https://www.original.com";
        String shortUrl = urlService.createShortUrl(originalUrl);
        Url url = urlRepository.findById(shortUrl).orElseThrow();

        Assertions.assertNotNull(shortUrl);
        Assertions.assertEquals(originalUrl, url.getUrl());
        Assertions.assertEquals(originalUrl, urlCacheRepository.findUrlByHash(shortUrl));
    }

    @Test
    void testGetUrlByHash() {
        String originalUrl = "https://www.original.com/test";
        String shortUrl = urlService.createShortUrl(originalUrl);

        String urlByHash = urlService.getUrlByHash(shortUrl);
        Assertions.assertEquals(originalUrl, urlByHash);
    }
}
