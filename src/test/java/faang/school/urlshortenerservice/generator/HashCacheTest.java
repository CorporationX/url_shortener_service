package faang.school.urlshortenerservice.generator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {

    @Mock
    private HashGenerator hashGenerator;

    @InjectMocks
    private HashCache hashCache;

    private final int capacity = 10;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(hashCache, "capacity", capacity);
        double lowThresholdPercentage = 30.0;
        ReflectionTestUtils.setField(hashCache, "lowThresholdPercentage", lowThresholdPercentage);
    }

    @Test
    void init_ShouldInitializeQueueWithGeneratedHashes() {
        List<String> generatedHashes = List.of("hash1", "hash2", "hash3");
        when(hashGenerator.getHashBatch(capacity)).thenReturn(generatedHashes);

        hashCache.init();

        Queue<String> hashes = (Queue<String>) ReflectionTestUtils.getField(hashCache, "hashes");
        assertNotNull(hashes);
        assertEquals(generatedHashes.size(), hashes.size());
        assertTrue(hashes.containsAll(generatedHashes));

        verify(hashGenerator, times(1)).getHashBatch(capacity);
    }

    @Test
    void getHash_ShouldReturnHashFromCache() {
        Queue<String> hashes = new ArrayBlockingQueue<>(capacity);
        hashes.addAll(List.of("hash1", "hash2", "hash3", "hash4"));
        ReflectionTestUtils.setField(hashCache, "hashes", hashes);

        String result = hashCache.getHash();

        assertEquals("hash1", result);
        assertEquals(3, hashes.size());

        verify(hashGenerator, never()).getHashBatchAsync(anyInt());
    }

    @Test
    void getHash_ShouldTriggerRefreshIfThresholdIsLow() {
        Queue<String> hashes = new ArrayBlockingQueue<>(capacity);
        hashes.addAll(List.of("hash1"));
        ReflectionTestUtils.setField(hashCache, "hashes", hashes);

        CompletableFuture<List<String>> futureHashes = CompletableFuture.completedFuture(List.of("newHash1", "newHash2"));
        when(hashGenerator.getHashBatchAsync(capacity)).thenReturn(futureHashes);

        String result = hashCache.getHash();

        assertEquals("hash1", result);
        verify(hashGenerator).getHashBatchAsync(capacity);
    }

    @Test
    void getHash_ShouldNotTriggerRefreshIfThresholdIsNotLow() {
        Queue<String> hashes = new ArrayBlockingQueue<>(capacity);
        hashes.addAll(List.of("hash1", "hash2", "hash3", "hash4", "hash5"));
        ReflectionTestUtils.setField(hashCache, "hashes", hashes);

        String result = hashCache.getHash();

        assertEquals("hash1", result);
        verify(hashGenerator, never()).getHashBatchAsync(anyInt());
    }

    @Test
    void triggerRefresh_ShouldAddNewHashesToCache() {
        Queue<String> testHashes = new ArrayBlockingQueue<>(capacity);
        ReflectionTestUtils.setField(hashCache, "hashes", testHashes);

        AtomicBoolean isRefreshing = new AtomicBoolean(false);
        ReflectionTestUtils.setField(hashCache, "isRefreshing", isRefreshing);

        CompletableFuture<List<String>> futureHashes = new CompletableFuture<>();
        when(hashGenerator.getHashBatchAsync(capacity)).thenReturn(futureHashes);

        String result = hashCache.getHash();

        assertNull(result);
        assertTrue(isRefreshing.get());

        verify(hashGenerator, times(1)).getHashBatchAsync(capacity);

        futureHashes.complete(List.of("newHash1", "newHash2"));

        assertFalse(isRefreshing.get());

        assertEquals(2, testHashes.size());
        assertTrue(testHashes.containsAll(List.of("newHash1", "newHash2")));
    }
}
