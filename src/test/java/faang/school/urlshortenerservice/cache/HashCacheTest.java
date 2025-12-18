package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.hash.HashRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class HashCacheTest {

    private final int queueCapacity = 1000;
    private final int hashQueuePercent = 20;

    @Mock
    private HashRepositoryImpl hashRepository;
    @Mock
    private HashGenerator hashGenerator;
    @Mock
    private ExecutorService executor;

    @InjectMocks
    private HashCache hashCache;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashCache, "queueCapacity", queueCapacity);
        ReflectionTestUtils.setField(hashCache, "hashQueuePercent", hashQueuePercent);
    }

    @Test
    void testInit_ShouldFillQueueOnStartup() {
        Mockito.when(hashRepository.getHashBatch(queueCapacity)).thenReturn(List.of("hash1", "hash2"));

        hashCache.init();

        assertEquals(2, hashCache.getHashQueue().size());
        verify(hashRepository).getHashBatch(queueCapacity);
        verify(hashGenerator).generateBatch();
    }

    @Test
    void testGetHash_WhenQueueNotEmpty_ShouldReturnHash() {
        hashCache.init();
        String expectedHash = "abc123";
        hashCache.getHashQueue().add(expectedHash);

        String result = hashCache.getHash();

        assertEquals(expectedHash, result);
        assertFalse(hashCache.getHashQueue().contains(expectedHash));
    }

    @Test
    void testGetHash_WhenQueueBelowThreshold_ShouldTriggerAsyncFill() {
        hashCache.init();

        Queue<String> smallQueue = new ArrayBlockingQueue<>(queueCapacity);
        for (int i = 0; i < 199; i++) {
            smallQueue.add("hash" + i);
        }
        ReflectionTestUtils.setField(hashCache, "hashQueue", smallQueue);

        hashCache.getHash();

        verify(executor).submit(Mockito.any(Runnable.class));
    }

    @Test
    void testGetHash_WhenQueueAboveThreshold_ShouldNotTriggerAsyncFill() {
        hashCache.init();

        Queue<String> largeQueue = new ArrayBlockingQueue<>(queueCapacity);
        for (int i = 0; i < 201; i++) {
            largeQueue.add("hash" + i);
        }
        ReflectionTestUtils.setField(hashCache, "hashQueue", largeQueue);

        hashCache.getHash();

        verify(executor, never()).submit(Mockito.any(Runnable.class));
    }
}