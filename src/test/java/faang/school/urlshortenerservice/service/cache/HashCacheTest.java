package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.service.HashService;
import faang.school.urlshortenerservice.service.generator.HashGenerator;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashCacheTest {
    @InjectMocks
    HashCache hashCache;

    @Mock
    HashService hashService;
    @Mock
    HashGenerator hashGenerator;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashCache, "cacheCapacity", 1000);
        ReflectionTestUtils.setField(hashCache, "coefficient", 2);
        ReflectionTestUtils.setField(hashCache, "minVolumePercent", 20);
    }

    @Test
    void testInit_WhenCacheIsEmpty_ShouldFillCache() {
        List<String> mockHashes = List.of("hash1", "hash2", "hash3", "hash4");
        int cacheCapacity = getCacheCapacity();
        int coefficient = getCoefficient();

        when(hashService.getHashBatch(cacheCapacity)).thenReturn(CompletableFuture.completedFuture(mockHashes));
        when(hashService.count()).thenReturn((long) cacheCapacity * coefficient + 1);

        hashCache.init();
        Queue<String> testHashQueue = getHashQueue();

        assertEquals(mockHashes.size(), testHashQueue.size());
        verify(hashGenerator, never()).generateBatch();
    }

    @Test
    void testGetHash_WhenCacheNeedsFilling_ShouldFillCacheAsync() {
        Queue<String> mockQueue = new ArrayBlockingQueue<>(getCacheCapacity());
        mockQueue.add("hash1");
        mockQueue.add("hash2");

        setHashQueue(mockQueue);
        List<String> newHashes = List.of("hash3", "hash4");
        int count = getCacheCapacity() - getHashQueue().size();
        when(hashService.getHashBatch(count)).thenReturn(CompletableFuture.completedFuture(newHashes));

        String hash = hashCache.getHash();

        assertEquals("hash1", hash);
        verify(hashService).getHashBatch(count);
    }

    @Test
    void testShouldGenerateNewHashes_WhenConditionMet_ShouldReturnTrue() {
        int count = getCacheCapacity() * getCoefficient() - 1;
        when(hashService.count()).thenReturn((long) count);

        boolean result = hashCache.shouldGenerateNewHashes();

        assertTrue(result);
    }

    @Test
    void testShouldGenerateNewHashes_WhenConditionNotMet_ShouldReturnFalse() {
        int count = getCacheCapacity() * getCoefficient() + 1;
        when(hashService.count()).thenReturn((long) count);

        boolean result = hashCache.shouldGenerateNewHashes();

        assertFalse(result);
    }

    @Test
    void testShouldFillCache_WhenCacheBelowThreshold_ShouldReturnTrue() {
        int cacheCapacity = getCacheCapacity();
        int minVolumePercent = getMinVolumePercent();
        int cacheVolume = (cacheCapacity * minVolumePercent / 100) - 1;
        Queue<String> mockQueue = new ArrayBlockingQueue<>(cacheCapacity);

        for (int i = 0; i < cacheVolume; i++) {
            mockQueue.add("hash" + i);
        }
        setHashQueue(mockQueue);

        boolean result = hashCache.shouldFillCache();

        assertTrue(result);
    }

    @Test
    void testShouldFillCache_WhenCacheAboveThreshold_ShouldReturnFalse() {
        int cacheCapacity = getCacheCapacity();
        int minVolumePercent = getMinVolumePercent();
        int cacheVolume = (cacheCapacity * minVolumePercent / 100) + 1;
        Queue<String> mockQueue = new ArrayBlockingQueue<>(cacheCapacity);

        for (int i = 0; i < cacheVolume; i++) {
            mockQueue.add("hash" + i);
        }
        setHashQueue(mockQueue);

        boolean result = hashCache.shouldFillCache();

        assertFalse(result);
    }

    public int getCacheCapacity() {
        return (int) ReflectionTestUtils.getField(hashCache, "cacheCapacity");
    }

    public int getCoefficient() {
        return (int) ReflectionTestUtils.getField(hashCache, "coefficient");
    }

    public int getMinVolumePercent() {
        return (int) ReflectionTestUtils.getField(hashCache, "minVolumePercent");
    }

    public Queue<String> getHashQueue() {
        return (Queue<String>) ReflectionTestUtils.getField(hashCache, "hashQueue");
    }

    public void setHashQueue(Queue<String> queue) {
        ReflectionTestUtils.setField(hashCache, "hashQueue", queue);
    }
}