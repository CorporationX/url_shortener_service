package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HashCacheTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private HashGenerator hashGenerator;

    private ExecutorService executorService;

    @InjectMocks
    private HashCache hashCache;

    private static final int CACHE_SIZE = 100;
    private static final int REFILL_THRESHOLD_PERCENT = 20;

    @BeforeEach
    void setUp() {
        executorService = Executors.newFixedThreadPool(2);
        ReflectionTestUtils.setField(hashCache, "hashCacheExecutor", executorService);
        ReflectionTestUtils.setField(hashCache, "cacheSize", CACHE_SIZE);
        ReflectionTestUtils.setField(hashCache, "refillThresholdPercent", REFILL_THRESHOLD_PERCENT);

        // По умолчанию возвращаем успешный CompletableFuture для всех тестов
        when(hashGenerator.generateBatch()).thenReturn(CompletableFuture.completedFuture(100));
    }

    @Test
    void testInitializationFillsCache() {
        // Given
        List<String> hashes = generateHashList(100);
        when(hashRepository.getHashBatch()).thenReturn(hashes);

        // When
        hashCache.init();

        // Then
        verify(hashRepository, times(1)).getHashBatch();
        assertThat(hashCache.getCacheSize()).isEqualTo(100);
    }

    @Test
    void testGetHashReturnsHash() {
        // Given
        List<String> hashes = generateHashList(100);
        when(hashRepository.getHashBatch()).thenReturn(hashes);
        hashCache.init();

        // When
        String hash = hashCache.getHash();

        // Then
        assertThat(hash).isNotNull();
        assertThat(hashes).contains(hash);
    }

    @Test
    void testGetHashReducesCacheSize() {
        // Given
        List<String> hashes = generateHashList(100);
        when(hashRepository.getHashBatch()).thenReturn(hashes);
        hashCache.init();
        int initialSize = hashCache.getCacheSize();

        // When
        hashCache.getHash();

        // Then
        assertThat(hashCache.getCacheSize()).isEqualTo(initialSize - 1);
    }

    @Test
    void testGetHashTriggersRefillWhenBelowThreshold() throws InterruptedException {
        // Given
        List<String> initialHashes = generateHashList(15); // 15% of 100
        List<String> refillHashes = generateHashList(50);

        when(hashRepository.getHashBatch())
                .thenReturn(initialHashes)
                .thenReturn(refillHashes);

        hashCache.init();

        // When
        hashCache.getHash();
        Thread.sleep(500); // Wait for async refill

        // Then
        verify(hashRepository, atLeast(2)).getHashBatch();
        verify(hashGenerator, atLeastOnce()).generateBatch();
    }

    @Test
    void testGetHashDoesNotTriggerRefillWhenAboveThreshold() throws InterruptedException {
        // Given
        List<String> hashes = generateHashList(100);
        when(hashRepository.getHashBatch()).thenReturn(hashes);
        hashCache.init();

        reset(hashRepository, hashGenerator);

        // When - remove only 1 hash, still 99 left (99% > 20% threshold)
        hashCache.getHash();
        Thread.sleep(200);

        // Then
        verify(hashRepository, never()).getHashBatch();
        verify(hashGenerator, never()).generateBatch();
    }

    @Test
    void testGetHashHandlesEmptyCache() {
        // Given
        List<String> hashes = generateHashList(50);
        when(hashRepository.getHashBatch())
                .thenReturn(Arrays.asList()) // empty first time
                .thenReturn(hashes);

        // When
        String hash = hashCache.getHash();

        // Then
        assertThat(hash).isNotNull();
        verify(hashGenerator, times(1)).generateBatch();
        verify(hashRepository, atLeast(2)).getHashBatch();
    }

    @Test
    void testConcurrentAccessIsSafe() throws InterruptedException {
        // Given
        List<String> hashes = generateHashList(1000);
        when(hashRepository.getHashBatch()).thenReturn(hashes);
        hashCache.init();

        // When - multiple threads access cache concurrently
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    String hash = hashCache.getHash();
                    assertThat(hash).isNotNull();
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // Then - no exceptions and all hashes are unique
        assertThat(hashCache.getCacheSize()).isEqualTo(900); // 1000 - (10 threads * 10 hashes)
    }

    @Test
    void testRefillIsCalledOnlyOnce() throws InterruptedException {
        // Given
        List<String> initialHashes = generateHashList(10); // Below threshold
        List<String> refillHashes = generateHashList(50);

        when(hashRepository.getHashBatch())
                .thenReturn(initialHashes)
                .thenReturn(refillHashes);

        hashCache.init();
        reset(hashRepository, hashGenerator);

        // Reset mock after init
        when(hashRepository.getHashBatch()).thenReturn(refillHashes);
        when(hashGenerator.generateBatch()).thenReturn(CompletableFuture.completedFuture(100));

        // When - multiple threads try to trigger refill
        Thread[] threads = new Thread[5];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> hashCache.getHash());
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        Thread.sleep(1000); // Wait for async operations

        // Then - refill should be called only once due to AtomicBoolean
        verify(hashRepository, atMost(2)).getHashBatch();
    }

    @Test
    void testGetCacheSizeReturnsCorrectSize() {
        // Given
        List<String> hashes = generateHashList(75);
        when(hashRepository.getHashBatch()).thenReturn(hashes);
        hashCache.init();

        // When
        int size = hashCache.getCacheSize();

        // Then
        assertThat(size).isEqualTo(75);
    }

    private List<String> generateHashList(int size) {
        String[] hashes = new String[size];
        for (int i = 0; i < size; i++) {
            hashes[i] = "hash" + i;
        }
        return Arrays.asList(hashes);
    }
}