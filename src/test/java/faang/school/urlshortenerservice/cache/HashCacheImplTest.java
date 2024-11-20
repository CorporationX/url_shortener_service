package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.cache.hash.HashCacheImpl;
import faang.school.urlshortenerservice.cache.hash.HashCacheProperty;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.generator.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HashCacheImplTest {

    private HashCacheImpl hashCache;
    private HashRepository hashRepository;
    private HashCacheProperty cacheProperty;

    @BeforeEach
    void setUp() {
        hashRepository = Mockito.mock(HashRepository.class);
        HashGenerator hashGenerator = Mockito.mock(HashGenerator.class);
        cacheProperty = Mockito.mock(HashCacheProperty.class);
        Executor executor = Executors.newSingleThreadExecutor();

        when(cacheProperty.getMaxQueueSize()).thenReturn(10);
        when(cacheProperty.getRefillPercent()).thenReturn(50);

        hashCache = new HashCacheImpl(executor, cacheProperty, hashRepository, hashGenerator);
    }

    @Test
    void testGetHash_ReturnsHashFromQueue() {
        hashCache.getHashQueue().addAll(List.of("hash1", "hash2"));

        String result = hashCache.getHash();

        assertEquals("hash1", result);
        assertEquals(1, hashCache.getHashQueue().size());
    }

    @Test
    void testGetHash_RefillsWhenBelowThreshold() throws InterruptedException {
        when(hashRepository.getHashBatch(anyInt())).thenReturn(List.of("hash3", "hash4"));
        hashCache.getHashQueue().add("hash1");

        String result = hashCache.getHash();

        Thread.sleep(100);

        assertEquals("hash1", result);
    }

    @Test
    void testInitialize_RefillsCacheOnStart() {
        when(hashRepository.getHashBatch(anyInt())).thenReturn(List.of("hash1", "hash2"));

        hashCache.initialize();
    }

    @Test
    void testRefillCacheAsync_DoesNotExceedMaxQueueSize() {
        when(hashRepository.getHashBatch(anyInt())).thenReturn(List.of("hash1", "hash2", "hash3"));
        when(cacheProperty.getMaxQueueSize()).thenReturn(5);

        hashCache.getHashQueue().addAll(List.of("hashA", "hashB", "hashC", "hashD"));
    }

    @Test
    void testGetHash_ReturnsNullWhenQueueEmpty() {
        String result = hashCache.getHash();

        assertNull(result);
    }

    @Test
    void testRefillCacheAsync_NoRefillWhenQueueIsFull() {
        when(hashRepository.getHashBatch(anyInt())).thenReturn(List.of("hash1", "hash2"));
        hashCache.getHashQueue().addAll(List.of("hashA", "hashB", "hashC", "hashD", "hashE"));

        assertEquals(5, hashCache.getHashQueue().size());
        verify(hashRepository, never()).getHashBatch(anyInt());
    }
}
