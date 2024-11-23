package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.service.HashService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashCacheTest {
    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private HashService hashService;

    @InjectMocks
    private HashCache hashCache;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(hashCache, "capacity", 10);
        ReflectionTestUtils.setField(hashCache, "capacityUsage", 50);
        ReflectionTestUtils.setField(hashCache, "batchSize", 5);
    }

    @Test
    public void testInitAndWarmCache() {
        when(hashGenerator.generateBatch(5))
                .thenReturn(CompletableFuture.completedFuture(null));

        when(hashService.getHashes(anyInt()))
                .thenReturn(CompletableFuture.completedFuture(List.of("hash1", "hash2", "hash3")));
        hashCache.init();

        verify(hashGenerator).generateBatch(5);
        verify(hashService).getHashes(anyInt());

        assertNotNull(hashCache.getHash());
    }

    @Test
    public void testGetCache() {
        Queue<String> mockCache = new ArrayBlockingQueue<>(10);
        mockCache.addAll(List.of("hash1", "hash2", "hash3"));
        ReflectionTestUtils.setField(hashCache, "hashesCache", mockCache);

        String hash = hashCache.getHash();
        String hash2 = hashCache.getHash();
        String hash3 = hashCache.getHash();

        assertEquals("hash1", hash);
        assertEquals("hash2", hash2);
        assertEquals("hash3", hash3);
    }

    @Test
    public void testCacheRefillOnThreshold() {
        Queue<String> mockCache = new ArrayBlockingQueue<>(10);
        mockCache.addAll(List.of("hash1", "hash2", "hash3"));
        ReflectionTestUtils.setField(hashCache, "hashesCache", mockCache);
        ReflectionTestUtils.setField(hashCache, "threshold", 5);
        AtomicBoolean isFillRequired = new AtomicBoolean(true);
        ReflectionTestUtils.setField(hashCache, "isFillingRequired", isFillRequired);

        when(hashGenerator.generateBatch(anyInt()))
                .thenReturn(CompletableFuture.completedFuture(null));

        when(hashService.getHashes(anyInt()))
                .thenReturn(CompletableFuture.completedFuture(List.of("hash4", "hash5")));

        String hash = hashCache.getHash();
        String hash2 = hashCache.getHash();
        String hash3 = hashCache.getHash();

        assertEquals("hash1", hash);
        assertEquals("hash2", hash2);
        assertEquals("hash3", hash3);

        assertTrue(mockCache.contains("hash4"));
        assertTrue(mockCache.contains("hash5"));
    }
}
