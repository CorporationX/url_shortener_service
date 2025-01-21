package faang.school.urlshortenerservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HashCacheTest {

    @Mock
    private ExecutorService executorService;

    @InjectMocks
    private HashCache hashCache;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(hashCache, "cacheSize", 10);
        ReflectionTestUtils.setField(hashCache, "thresholdPercentage", 20.0);
        hashCache.initializeQueue();
    }

    @Test
    void getHashAboveThresholdSuccessTest() {
        LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>(Arrays.asList("hash1", "hash2"));
        ReflectionTestUtils.setField(hashCache, "hashQueue", queue);

        String hash = hashCache.getHash();

        assertEquals("hash1", hash);
        assertEquals(1, queue.size());
    }

    @Test
    void getHashBelowThresholdSuccessTest() {
        LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>(Collections.emptyList());
        ReflectionTestUtils.setField(hashCache, "hashQueue", queue);
        AtomicBoolean isRefreshing = new AtomicBoolean(false);
        ReflectionTestUtils.setField(hashCache, "isRefreshing", isRefreshing);

        doAnswer(invocation -> {
            queue.addAll(Arrays.asList("hash1", "hash2"));
            return null;
        }).when(executorService).submit(any(Runnable.class));

        String hash = hashCache.getHash();

        assertEquals("hash1", hash);
        assertTrue(isRefreshing.get());

        verify(executorService).submit(any(Runnable.class));
    }

    @Test
    void getHashEmptyQueueAndTimeoutFailTest() {
        LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>(Collections.emptyList());
        ReflectionTestUtils.setField(hashCache, "hashQueue", queue);

        String hash = hashCache.getHash();

        assertNull(hash);
    }
}