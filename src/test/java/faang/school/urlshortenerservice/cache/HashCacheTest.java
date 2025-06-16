package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.exception.CacheInitializationException;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.service.hash.HashService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test cases of HashCacheTest")
public class HashCacheTest {

    private static final int CACHE_CAPACITY = 10;
    private static final double REFILL_THRESHOLD = 0.2;

    @Mock
    private HashService hashService;

    @Mock
    private HashGenerator hashGenerator;

    @InjectMocks
    private HashCache hashCache;

    private ExecutorService executorService;

    private AtomicBoolean isRefilledCache;

    private BlockingQueue<String> localCache;

    @BeforeEach
    public void setUp() {
        localCache = new ArrayBlockingQueue<>(CACHE_CAPACITY);
        isRefilledCache = new AtomicBoolean(false);
        executorService = Executors.newSingleThreadExecutor();

        ReflectionTestUtils.setField(hashCache, "executorService", executorService);
        ReflectionTestUtils.setField(hashCache, "cacheCapacity", CACHE_CAPACITY);
        ReflectionTestUtils.setField(hashCache, "refillThreshold", REFILL_THRESHOLD);
        ReflectionTestUtils.setField(hashCache, "localCache", localCache);
        ReflectionTestUtils.setField(hashCache, "isRefilledCache", isRefilledCache);
    }

    @Test
    @DisplayName("cacheInit - cache init error")
    public void testCacheInitWithError() {
        when(hashService.getHashBatch(anyInt())).thenThrow(new RuntimeException());

        Exception exception = assertThrows(CacheInitializationException.class, () -> hashCache.cacheInit());

        assertTrue(exception.getMessage().startsWith("Cache init error:"));
    }

    @Test
    @DisplayName("cacheInit - successful without hashes generation")
    public void testCacheInitSuccessWithoutHashesGeneration() {
        List<String> hashes = List.of("hash1", "hash2", "hash3");
        when(hashService.getHashBatch(CACHE_CAPACITY)).thenReturn(hashes);

        hashCache.cacheInit();

        BlockingQueue<String> actualCache =
                (BlockingQueue<String>) ReflectionTestUtils.getField(hashCache, "localCache");

        assertEquals(hashes.size(), actualCache.size());
        assertTrue(actualCache.containsAll(hashes));

        verify(hashService, times(1)).isNeedGenerateHash();
        verify(hashService, times(1)).getHashBatch(CACHE_CAPACITY);
        verify(hashGenerator, never()).generateBatch();
    }

    @Test
    @DisplayName("cacheInit - successful with hashes generation")
    public void testCacheInitSuccessWithHashesGeneration() {
        List<String> hashes = List.of("hash1", "hash2", "hash3");
        when(hashService.isNeedGenerateHash()).thenReturn(true);
        when(hashService.getHashBatch(CACHE_CAPACITY)).thenReturn(hashes);

        hashCache.cacheInit();

        BlockingQueue<String> actualCache =
                (BlockingQueue<String>) ReflectionTestUtils.getField(hashCache, "localCache");

        assertEquals(hashes.size(), actualCache.size());
        assertTrue(actualCache.containsAll(hashes));

        verify(hashService, times(1)).isNeedGenerateHash();
        verify(hashGenerator, times(1)).generateBatch();
        verify(hashService, times(1)).getHashBatch(CACHE_CAPACITY);
    }

    @Test
    @DisplayName("getHash - empty cache")
    public void testGetHashEmptyCache() {
        assertTrue(localCache.isEmpty());

        Optional<String> actualHash = hashCache.getHash();

        assertFalse(actualHash.isPresent());
    }

    @Test
    @DisplayName("getHash - refill failed")
    public void testGetHashRefillFailed() throws InterruptedException {
        when(hashService.getHashBatch(anyInt())).thenThrow(new RuntimeException("DB error"));

        Optional<String> actualHash = hashCache.getHash();
        Thread.sleep(500);

        assertFalse(isRefilledCache.get());
        assertTrue(actualHash.isEmpty());
    }

    @Test
    @DisplayName("getHash - cache size below threshold")
    public void testGetHashCacheSizeBelowThreshold() throws InterruptedException {
        String expectedHash = "hash";
        localCache.add(expectedHash);
        List<String> expectedHashes = List.of("hash1", "hash2", "hash3");
        when(hashService.getHashBatch(CACHE_CAPACITY - 1)).thenReturn(expectedHashes);

        Optional<String> actualHash = hashCache.getHash();
        Thread.sleep(500);

        assertEquals(expectedHash, actualHash.get());
        assertFalse(isRefilledCache.get());
        assertTrue(localCache.containsAll(expectedHashes));
        verify(hashGenerator, never()).generateBatchAsync();
    }

    @Test
    @DisplayName("getHash - generation hashes")
    public void testGetHashWithGenerationHashes() throws InterruptedException {
        when(hashService.isNeedGenerateHash()).thenReturn(true);

        hashCache.getHash();
        Thread.sleep(500);

        verify(hashGenerator, times(1)).generateBatchAsync();
    }

    @Test
    @DisplayName("getHash - success")
    public void testGetHashSuccess() {
        String expectedHash = "hash";
        localCache.add(expectedHash);

        Optional<String> actualHash = hashCache.getHash();

        assertTrue(actualHash.isPresent());
        assertEquals(expectedHash, actualHash.get());
        assertTrue(localCache.isEmpty());
    }
}
