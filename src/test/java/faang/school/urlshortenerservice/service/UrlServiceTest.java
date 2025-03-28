package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.HashCache;
import faang.school.urlshortenerservice.util.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private ExecutorService executorService;

    @Mock
    private HashGenerator hashGenerator;

    private HashCache hashCache;

    @BeforeEach
    void setUp() {
        hashCache = new HashCache(hashRepository, executorService, hashGenerator);

        ReflectionTestUtils.setField(hashCache, "maxCacheSize", 100);
        ReflectionTestUtils.setField(hashCache, "thresholdPercent", 20);
        ReflectionTestUtils.setField(hashCache, "batchSize", 50);
    }

    @Test
    void init_shouldRefillCache() {
        when(hashRepository.findTopN(anyInt())).thenReturn(Arrays.asList(1L, 2L, 3L));

        hashCache.init();

        verify(hashRepository).findTopN(50);
        verify(hashGenerator).generateBatch();

        @SuppressWarnings("unchecked")
        ConcurrentLinkedQueue<String> queue = (ConcurrentLinkedQueue<String>) ReflectionTestUtils.getField(hashCache, "hashQueue");
        assertEquals(3, queue.size());
    }

    @Test
    void getHash_whenCacheAboveThreshold_shouldReturnHashWithoutRefill() {
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        for (int i = 1; i <= 25; i++) {
            queue.add("hash" + i);
        }
        ReflectionTestUtils.setField(hashCache, "hashQueue", queue);

        String hash = hashCache.getHash();

        assertEquals("hash1", hash);
        verify(executorService, never()).submit(any(Runnable.class));
    }

    @Test
    void getHash_whenCacheBelowThreshold_shouldRefillCache() {
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        queue.add("hash1");
        ReflectionTestUtils.setField(hashCache, "hashQueue", queue);
        ReflectionTestUtils.setField(hashCache, "isRefilling", new AtomicBoolean(false));

        when(hashRepository.findTopN(anyInt())).thenReturn(Arrays.asList(10L, 20L, 30L));

        doAnswer(invocation -> {
            ((Runnable)invocation.getArgument(0)).run();
            return null;
        }).when(executorService).submit(any(Runnable.class));

        String hash = hashCache.getHash();

        assertEquals("hash1", hash);
        verify(executorService).submit(any(Runnable.class));
        verify(hashRepository).findTopN(50);
        verify(hashGenerator).generateBatch();
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

    @Test
    void refillCache_whenRepositoryReturnsEmptyList_shouldNotAddToQueue() {
        when(hashRepository.findTopN(anyInt())).thenReturn(Collections.emptyList());
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        ReflectionTestUtils.setField(hashCache, "hashQueue", queue);

        hashCache.init();

        assertTrue(queue.isEmpty());
        verify(hashGenerator).generateBatch();
    }

    @Test
    void refillCache_shouldHandleExceptions() {
        when(hashRepository.findTopN(anyInt())).thenThrow(new RuntimeException("Test exception"));

        hashCache.init();

        AtomicBoolean isRefilling = (AtomicBoolean) ReflectionTestUtils.getField(hashCache, "isRefilling");
        assertFalse(isRefilling.get());
    }

    @Test
    void refillCache_shouldBeCalledOnlyOnce() {
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        ReflectionTestUtils.setField(hashCache, "hashQueue", queue);

        ReflectionTestUtils.setField(hashCache, "isRefilling", new AtomicBoolean(true));

        hashCache.getHash();

        verify(executorService, never()).submit(any(Runnable.class));
    }
}