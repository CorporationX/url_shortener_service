package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.model.entity.Hash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashCacheTest {

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private ThreadPoolTaskExecutor cacheLoaderPool;

    @Mock
    private HashService hashService;

    @InjectMocks
    private HashCache hashCache;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashCache, "cacheCapacity", 6);
        ReflectionTestUtils.setField(hashCache, "threshold", 20.0);
        ReflectionTestUtils.setField(hashCache, "initialGenerationBatchSize", 50);
    }

    @Test
    void init_ShouldInitializeCacheAndLoadHashes() {
        List<Hash> hashes = Arrays.asList(
                new Hash("hash1"),
                new Hash("hash2")
        );
        when(hashService.getHashesBatch(anyInt())).thenReturn(hashes);

        hashCache.init();

        verify(hashGenerator).generateHashes(50);
        verify(hashService).getHashesBatch(anyInt());
    }

    @Test
    void getHash_WhenCacheIsAboveThreshold_ShouldReturnHashWithoutRefilling() {
        ReflectionTestUtils.setField(hashCache, "cache",
                new LinkedBlockingQueue<>(Arrays.asList("hash1", "hash2", "hash3", "hash4")));

        String result = hashCache.getHash();

        assertEquals("hash1", result);
        verify(hashGenerator, never()).generateHashesAsync(anyInt());
    }

    @Test
    void getHash_WhenCacheIsBelowThreshold_ShouldTriggerRefill() {
        LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
        queue.offer("hash1");
        ReflectionTestUtils.setField(hashCache, "cache", queue);

        String result = hashCache.getHash();

        assertEquals("hash1", result);
        verify(hashGenerator).generateHashesAsync(anyInt());
    }

    @Test
    void loadHashToCache_ShouldLoadHashesFromService() {
        List<Hash> hashes = Arrays.asList(
                new Hash("hash1"),
                new Hash("hash2")
        );
        ReflectionTestUtils.setField(hashCache, "cache", new LinkedBlockingQueue<>());
        when(hashService.getHashesBatch(anyInt())).thenReturn(hashes);

        hashCache.loadHashToCache();

        verify(hashService).getHashesBatch(anyInt());
        BlockingQueue<String> cache = (BlockingQueue<String>) ReflectionTestUtils.getField(hashCache, "cache");
        assertEquals(2, cache.size());
    }
}
