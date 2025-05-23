package faang.school.urlshortenerservice.hash_generator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashCacheTest {

    @Mock
    private HashGenerator hashGenerator;

    private HashCache hashCache;

    private final int TEST_CAPACITY = 100;
    private final int TEST_THRESHOLD = 20;

    @BeforeEach
    void setUp() {
        hashCache = new HashCache(hashGenerator);
        ReflectionTestUtils.setField(hashCache, "storageCapacity", TEST_CAPACITY);
        ReflectionTestUtils.setField(hashCache, "minThresholdPercent", TEST_THRESHOLD);
    }

    @Test
    void getHash_whenCacheAboveThreshold_shouldReturnHashWithoutRefill() {
        // Arrange
        var generatedHashes = generateTestHashes(TEST_CAPACITY);

        when(hashGenerator.getHashes(TEST_CAPACITY)).thenReturn(generatedHashes);
        hashCache.init();

        // Act
        var hash = hashCache.getHash();

        // Assert
        assertNotNull(hash);
        assertTrue(generatedHashes.contains(hash));
        verify(hashGenerator, never()).getHashesAsync(anyInt());
    }

    @Test
    void getHash_whenCacheBelowThreshold_shouldTriggerAsyncRefill() {
        // Arrange
        int initialSize = TEST_CAPACITY * TEST_THRESHOLD / 100 - 10;
        Queue<String> hashes = new ArrayBlockingQueue<>(TEST_CAPACITY);
        hashes.addAll(generateTestHashes(initialSize));
        ReflectionTestUtils.setField(hashCache, "hashes", hashes);

        CompletableFuture<List<String>> future = CompletableFuture.completedFuture(
                generateTestHashes(TEST_CAPACITY));
        when(hashGenerator.getHashesAsync(TEST_CAPACITY)).thenReturn(future);

        // Act
        var hash = hashCache.getHash();

        // Assert
        assertNotNull(hash);
        verify(hashGenerator, times(1)).getHashesAsync(TEST_CAPACITY);
    }

    @Test
    void getHash_whenAlreadyFilling_shouldNotTriggerAnotherRefill() {
        // Arrange
        Queue<String> hashes = new ArrayBlockingQueue<>(TEST_CAPACITY);
        hashes.addAll(generateTestHashes(TEST_THRESHOLD - 5));
        ReflectionTestUtils.setField(hashCache, "hashes", hashes);

        CompletableFuture<List<String>> future = new CompletableFuture<>();
        when(hashGenerator.getHashesAsync(TEST_CAPACITY)).thenReturn(future);

        // Act
        hashCache.getHash();
        hashCache.getHash();

        future.complete(generateTestHashes(TEST_CAPACITY));

        // Assert
        verify(hashGenerator, times(1)).getHashesAsync(anyLong());
    }

    @Test
    void getHash_whenCacheEmpty_shouldReturnNull() {
        // Arrange
        Queue<String> emptyHashes = new ArrayBlockingQueue<>(TEST_CAPACITY);
        ReflectionTestUtils.setField(hashCache, "hashes", emptyHashes);

        CompletableFuture<List<String>> future = new CompletableFuture<>();
        when(hashGenerator.getHashesAsync(anyLong())).thenReturn(future);

        // Act
        var hash = hashCache.getHash();

        // Assert
        assertNull(hash);
        verify(hashGenerator, times(1)).getHashesAsync(TEST_CAPACITY);
    }

    @Test
    void getHash_whenAsyncRefillCompletes_shouldAddHashesToCache() throws Exception {
        // Arrange
        Queue<String> hashes = new ArrayBlockingQueue<>(TEST_CAPACITY);
        ReflectionTestUtils.setField(hashCache, "hashes", hashes);

        CompletableFuture<List<String>> future = new CompletableFuture<>();
        when(hashGenerator.getHashesAsync(TEST_CAPACITY)).thenReturn(future);

        // Act
        hashCache.getHash();

        var newHashes = generateTestHashes(TEST_CAPACITY);
        future.complete(newHashes);
        Thread.sleep(100);
        CompletableFuture.allOf(future).join();

        // Assert
        var updatedHashes = (Queue<String>) ReflectionTestUtils.getField(hashCache, "hashes");
        assertEquals(TEST_CAPACITY, updatedHashes.size());
    }

    private List<String> generateTestHashes(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> "hash" + i)
                .collect(Collectors.toList());
    }
}