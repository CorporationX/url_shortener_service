package faang.school.urlshortenerservice.service.url_shortener;

import faang.school.urlshortenerservice.config.async.ThreadPool;
import faang.school.urlshortenerservice.dto.url.UrlDto;
import faang.school.urlshortenerservice.properties.HashCacheQueueProperties;
import faang.school.urlshortenerservice.repository.url.impl.UrlRepositoryImpl;
import faang.school.urlshortenerservice.service.hash_cache.HashCache;
import jakarta.persistence.EntityNotFoundException;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private HashCacheQueueProperties queueProp;

    @Mock
    private UrlRepositoryImpl urlRepository;

    @Mock
    private RedisCacheManager cacheManager;

    @Mock
    private ThreadPool threadPool;

    @Mock
    private HashCache hashCache;

    @Mock
    private Cache hashCacheMock;

    @InjectMocks
    private UrlService urlService;

    private String hash;
    private String url;
    private UrlDto urlDto;
    private ThreadPoolTaskExecutor executor;
    private Queue<String> localCacheQueue;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        localCacheQueue = new LinkedBlockingQueue<>(10);
        String domain = "http://localhost:8080/api/v1/urls/";
        hash = "hash";
        url = domain + hash;

        urlDto = UrlDto.builder()
                .url("https://LongUrl")
                .build();

        Field domainField = UrlService.class.getDeclaredField("domain");
        domainField.setAccessible(true);
        domainField.set(urlService, domain);

        executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("async-hash-fill-exec-test");
        executor.initialize();
    }

    @AfterEach
    void tearDown() {
        ThreadPoolTaskExecutor executor = threadPool.hashGeneratorExecutor();
        if (executor != null) {
            executor.shutdown();
        }
    }

    @Test
    public void shortenUrlTest() {
        localCacheQueue.addAll(List.of(hash, "hash1", "hash2", "hash3", "hash4", "hash5", "hash6"));

        when(hashCache.getLocalHashCache()).thenReturn(localCacheQueue);
        doNothing().when(urlRepository).save(hash, urlDto.getUrl());
        when(cacheManager.getCache(hash)).thenReturn(hashCacheMock);
        doNothing().when(hashCacheMock).put(eq(hash), eq(urlDto.getUrl()));

        String shortenedUrl = urlService.shortenUrl(urlDto);

        verify(hashCacheMock, times(1)).put(eq(hash), eq(urlDto.getUrl()));
        verify(hashCache, times(2)).getLocalHashCache();

        assertEquals(url, shortenedUrl);
    }

    @Test
    public void shortenUrlStartFillingCacheTest() {
        localCacheQueue.add(hash);

        when(queueProp.getMaxQueueSize()).thenReturn(10);
        when(queueProp.getPercentageToStartFill()).thenReturn(90);
        when(hashCache.getLocalHashCache()).thenReturn(localCacheQueue);
        when(threadPool.hashCacheFillExecutor()).thenReturn(executor);
        when(cacheManager.getCache(hash)).thenReturn(hashCacheMock);
        doNothing().when(hashCacheMock).put(eq(hash), eq(urlDto.getUrl()));

        String result = urlService.shortenUrl(urlDto);

        Awaitility.await()
                .atMost(30, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    verify(hashCache, times(1)).fillCache();
                    verify(urlRepository, times(1)).save(hash, urlDto.getUrl());

                    assertEquals(url, result);
                    assertEquals(0, localCacheQueue.size());
                });
    }

    @Test
    public void getOriginalUrlNotPresentInCacheRepositoryTest() {
        when(urlRepository.findOriginalUrlByHash(hash)).thenReturn(Optional.ofNullable(urlDto.getUrl()));

        String originalUrl = urlService.getOriginalUrl(hash);

        verify(urlRepository, times(1)).findOriginalUrlByHash(hash);

        assertEquals(urlDto.getUrl(), originalUrl);
    }

    @Test
    public void shortenUrlThrowsExceptionTest() {
        when(urlRepository.findOriginalUrlByHash(hash)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> urlService.getOriginalUrl(hash));

        verify(urlRepository, times(1)).findOriginalUrlByHash(hash);
    }
}