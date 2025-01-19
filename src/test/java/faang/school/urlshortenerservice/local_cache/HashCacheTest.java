package faang.school.urlshortenerservice.local_cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@EnableAsync
@SpringBootTest
class HashCacheTest {

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private HashGenerator hashGenerator;

    private int cacheSize;
    private int percent;
    private HashCache hashCache;

    @BeforeEach
    void setUp() {
        cacheSize = 5;
        percent = 20;
        hashCache = new HashCache(transactionService, hashGenerator);
        ReflectionTestUtils.setField(hashCache, "cacheSize", cacheSize);
        ReflectionTestUtils.setField(hashCache, "percent", percent);
        ReflectionTestUtils.setField(hashCache, "cache", new ArrayBlockingQueue<>(cacheSize));
    }

    @Test
    void testInit() {
        List<String> mockHashes = List.of("hash1", "hash2", "hash3");
        when(transactionService.saveHashBatch(5)).thenReturn(mockHashes);

        hashCache.init();

        Queue<String> cache = (Queue<String>) ReflectionTestUtils.getField(hashCache, "cache");
        assertNotNull(cache);
        assertEquals(3, cache.size());
        assertTrue(cache.containsAll(mockHashes));

        verify(transactionService, times(1)).saveHashBatch(5);
    }

    @Test
    void testGetHashWhenCacheIsSufficient() {
        Queue<String> cache = (Queue<String>) ReflectionTestUtils.getField(hashCache, "cache");
        cache.addAll(List.of("hash1", "hash2"));

        String result = hashCache.getHash();

        assertEquals("hash1", result, "The returned hash should be the one polled from the cache.");

        verifyNoInteractions(hashGenerator);
    }
}