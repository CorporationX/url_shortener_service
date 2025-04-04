package faang.school.urlshortenerservice.utils;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.HashCache;
import faang.school.urlshortenerservice.service.HashGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashCacheTest {
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

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(executorService).submit(any(Runnable.class));
    }

    @Test
    void getHash_whenCacheBelowThreshold_shouldRefillCache() throws InterruptedException {
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        queue.add("hash1");
        ReflectionTestUtils.setField(hashCache, "hashQueue", queue);
        ReflectionTestUtils.setField(hashCache, "isRefilling", new AtomicBoolean(false));

        when(hashRepository.getAvailableHashes(anyInt())).thenReturn(Arrays.asList("hash2", "hash3", "hash4"));
        when(hashGeneratorService.getAvailableHashesCount()).thenReturn(100); // Below minimum
        when(redisLockRegistry.obtain(anyString())).thenReturn(lock);
        when(lock.tryLock(anyLong(), any(TimeUnit.class))).thenReturn(true);

        String hash = hashCache.getHash();

        assertEquals("hash1", hash);
        verify(executorService).submit(any(Runnable.class));
        verify(hashRepository).getUniqueNumbers(50);
        verify(hashGeneratorService).generateBatch();
    }

    @Test
    void getHashCache_shouldReturnMultipleHashes() {
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        queue.add("hash1");
        queue.add("hash2");
        queue.add("hash3");
        ReflectionTestUtils.setField(hashCache, "hashQueue", queue);

        List<String> hashes = hashCache.getHashCache(Arrays.asList(1L, 2L));

        assertEquals(2, hashes.size());
        assertEquals("hash1", hashes.get(0));
        assertEquals("hash2", hashes.get(1));
    }
}
