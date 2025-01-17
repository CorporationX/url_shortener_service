package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.config.CachePropertiesConfig;
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
        CachePropertiesConfig cachePropertiesConfig = new CachePropertiesConfig(10, 0.2, 0.8, 100);
        cache = new ArrayBlockingQueue<>(cachePropertiesConfig.size());
        hashCache = new HashCache(hashRepository, hashGenerator, threadPoolTaskExecutor, cachePropertiesConfig);
        ReflectionTestUtils.setField(hashCache, "cache", cache);
    }

    @Test
    void testInit_ShouldInitializeCacheAndWarmUp() {
        when(hashRepository.count()).thenReturn(20L);

        hashCache.init();

        assertEquals(10, cache.remainingCapacity());
        verify(hashRepository, times(2)).count();
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
