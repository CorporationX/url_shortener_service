package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private ExecutorService executorService;

    @Mock
    private HashGeneratorService hashGeneratorService;

    @Mock
    private RedisLockRegistry redisLockRegistry;

    @Mock
    private Lock lock;

    private HashCache hashCache;

    @BeforeEach
    void setUp() {
        hashCache = new HashCache(hashRepository, executorService, hashGeneratorService, redisLockRegistry);

        ReflectionTestUtils.setField(hashCache, "maxCacheSize", 100);
        ReflectionTestUtils.setField(hashCache, "thresholdPercent", 20);
        ReflectionTestUtils.setField(hashCache, "batchSize", 50);
        ReflectionTestUtils.setField(hashCache, "minDbAvailable", 500);

        when(redisLockRegistry.obtain(anyString())).thenReturn(lock);
    }

    @Test
    void init_shouldRefillCache() {
        when(hashRepository.getAvailableHashes(anyInt())).thenReturn(Arrays.asList("hash1", "hash2", "hash3"));

        hashCache.init();

        verify(hashRepository, times(2)).getAvailableHashes(anyInt());

        @SuppressWarnings("unchecked")
        ConcurrentLinkedQueue<String> queue = (ConcurrentLinkedQueue<String>)
                ReflectionTestUtils.getField(hashCache, "hashQueue");
        assertEquals(3, queue.size());
    }

    @Test
    void getHash_whenCacheAboveThreshold_shouldReturnHashWithoutRefill() {
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        queue.add("hash1");
        ReflectionTestUtils.setField(hashCache, "hashQueue", queue);

        String hash = hashCache.getHash();

        assertEquals("hash1", hash);
        verify(executorService, never()).submit(any(Runnable.class));
    }

    @Test
    void refillCache_whenRepositoryReturnsEmptyList_shouldNotAddToQueue() {
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        for (int i = 1; i <= 5; i++) {
            queue.add("hash" + i);
        }
        ReflectionTestUtils.setField(hashCache, "hashQueue", queue);

        List<String> hashes = hashCache.getHashCache(Arrays.asList(1L, 2L, 3L));

        assertEquals(3, hashes.size());
        assertEquals("hash1", hashes.get(0));
        assertEquals("hash2", hashes.get(1));
        assertEquals("hash3", hashes.get(2));
    }

    @Test
    void refillCache_whenRepositoryReturnsEmptyList_shouldAttemptGeneration() throws Exception {
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        ReflectionTestUtils.setField(hashCache, "hashQueue", queue);

        when(hashRepository.getAvailableHashes(anyInt())).thenReturn(Collections.emptyList());
        when(hashGeneratorService.getAvailableHashesCount()).thenReturn(100); // Below minimum
        when(redisLockRegistry.obtain(anyString())).thenReturn(lock);
        when(lock.tryLock(anyLong(), any(TimeUnit.class))).thenReturn(true);

        // Call the internal method using reflection
        ReflectionTestUtils.invokeMethod(hashCache, "refillCache");

        verify(hashGeneratorService).generateBatch();
    }

    @Test
    void refillCache_shouldHandleExceptions() {
        when(hashRepository.getUniqueNumbers(anyInt())).thenThrow(new RuntimeException("Test exception"));

        ReflectionTestUtils.invokeMethod(hashCache, "refillCache");

        AtomicBoolean isRefilling = (AtomicBoolean) ReflectionTestUtils.getField(hashCache, "isRefilling");
        assertFalse(isRefilling.get());
    }

    @Test
    void refillCache_shouldSetIsRefillingFlag() {
        ReflectionTestUtils.setField(hashCache, "isRefilling", new AtomicBoolean(true));

        hashCache.getHash();

        verify(executorService, never()).submit(any(Runnable.class));
    }
}