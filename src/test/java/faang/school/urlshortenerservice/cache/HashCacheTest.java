package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.config.CacheProperties;
import faang.school.urlshortenerservice.config.DatabaseProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {

    @InjectMocks
    private HashCache hashCache;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    private BlockingQueue<String> cache;

    @BeforeEach
    public void setUp() {
        CacheProperties cacheProperties = new CacheProperties(10, 20, 80);
        DatabaseProperties databaseProperties = new DatabaseProperties(20);
        cache = new ArrayBlockingQueue<>(cacheProperties.maxCacheSize());
        hashCache = new HashCache(hashRepository, hashGenerator, threadPoolTaskExecutor, cacheProperties, databaseProperties);
        ReflectionTestUtils.setField(hashCache, "cache", cache);
    }

    @Test
    void testInit_ShouldInitializeCacheAndWarmUp() {
        when(hashRepository.count()).thenReturn(20L);

        hashCache.init();

        assertEquals(10, cache.remainingCapacity());
        verify(hashRepository, times(1)).count();
    }

    @Test
    @DisplayName("Take cache: success case")
    void testTakeCache_Success() {
        cache.add("1");

        String hash = hashCache.takeCache();

        assertEquals("1", hash);
        verify(hashRepository, times(1)).count();
    }
}
