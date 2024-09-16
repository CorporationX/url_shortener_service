package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {

    @Mock
    private HashRepository hashRepository;
    @Mock
    private HashGenerator hashGenerator;
    private HashCache hashCache;

    @BeforeEach
    void setUp() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        hashCache = new HashCache(hashGenerator, hashRepository, executorService);

        ReflectionTestUtils.setField(hashCache, "cacheSize", 1000);
        ReflectionTestUtils.setField(hashCache, "thresholdPercent", 20);
    }

    private List<String> generateLargeDataSet(int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> "hash" + i)
                .toList();
    }

    @Test
    void testGetHash_WhenCacheEmpty_ShouldTriggerRefillOnce() {
        List<String> largeDataSet = generateLargeDataSet(1000);
        when(hashRepository.getHashBatch()).thenReturn(largeDataSet);
        doNothing().when(hashGenerator).generateBatch();

        String firstHash = hashCache.getHash();

        assertNull(firstHash);

        await().atMost(10, TimeUnit.SECONDS).until(() -> {
            String hash = hashCache.getHash();
            return hash != null && hash.startsWith("hash");
        });

        String secondHash = hashCache.getHash();
        assertNotNull(secondHash);
        assertTrue(secondHash.startsWith("hash"));

        verify(hashRepository, times(1)).getHashBatch();
        verify(hashGenerator, times(1)).generateBatch();
    }

    @Test
    void testGetHash_ShouldTriggerRefillTwice() {
        List<String> largeDataSet = generateLargeDataSet(1000);
        when(hashRepository.getHashBatch()).thenReturn(largeDataSet);
        doNothing().when(hashGenerator).generateBatch();

        hashCache.getHash();

        await().atMost(10, TimeUnit.SECONDS).until(() -> {
            String hash = hashCache.getHash();
            return hash != null && hash.startsWith("hash");
        });

        for (int i = 0; i < 801; i++) {
            assertNotNull(hashCache.getHash());
        }

        String triggeringHash = hashCache.getHash();
        assertNotNull(triggeringHash);

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(hashRepository, times(2)).getHashBatch();
            verify(hashGenerator, times(2)).generateBatch();
        });

        ConcurrentLinkedQueue<?> cacheField = (ConcurrentLinkedQueue<?>) ReflectionTestUtils.getField(hashCache, "cache");
        assertNotNull(cacheField);
        assertFalse(cacheField.isEmpty());
    }

    @Test
    void testGetHash_WhenCacheAboveThreshold_ShouldNotTriggerRefill() {
        for (int i = 0; i < 300; i++) {
            ((ConcurrentLinkedQueue<String>) ReflectionTestUtils.getField(hashCache, "cache")).offer("hash" + i);
        }

        String hash = hashCache.getHash();
        assertNotNull(hash);
        assertTrue(hash.startsWith("hash"));

        verify(hashRepository, never()).getHashBatch();
        verify(hashGenerator, never()).generateBatch();
    }

    @Test
    void testGetHash_WhenRefillFails_ShouldContinueReturningAvailableHashes() {
        List<String> largeDataSet = generateLargeDataSet(1000);
        when(hashRepository.getHashBatch()).thenReturn(largeDataSet);
        doNothing().when(hashGenerator).generateBatch();

        when(hashRepository.getHashBatch())
                .thenReturn(IntStream.range(0, 1000).mapToObj(i -> "hash" + i).toList())
                .thenThrow(new RuntimeException("DB Error"));

        hashCache.getHash();

        await().atMost(10, TimeUnit.SECONDS).until(() ->
                ((ConcurrentLinkedQueue<?>) ReflectionTestUtils.getField(hashCache, "cache")).size() > 0
        );

        for (int i = 0; i < 801; i++) {
            assertNotNull(hashCache.getHash());
        }

        String hash = hashCache.getHash();
        assertNotNull(hash);

        for (int i = 0; i < 198; i++) {
            assertNotNull(hashCache.getHash());
        }

        assertNull(hashCache.getHash());

        verify(hashRepository, times(2)).getHashBatch();
    }
}