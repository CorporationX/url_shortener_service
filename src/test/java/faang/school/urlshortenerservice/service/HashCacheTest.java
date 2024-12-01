package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
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
        int cacheSize = 10;
        double threshold = 0.2;
        hashCache = new HashCache(hashRepository, hashGenerator, cacheSize, threshold);
    }

    @Test
    void getHash_shouldReturnHashFromCache() {
        ConcurrentLinkedQueue<String> mockQueue = new ConcurrentLinkedQueue<>();
        mockQueue.add("hash1");
        ReflectionTestUtils.setField(hashCache, "hashQueue", mockQueue);

        String hash = hashCache.getHash();

        assertEquals("hash1", hash);
    }

    @Test
    void getHash_shouldTriggerAsyncFillWhenCacheIsLow() {
        when(hashRepository.getHashBatch(anyInt())).thenReturn(List.of("hash2", "hash3"));
        hashCache.getHash();
        verify(hashGenerator, atLeastOnce()).generateBatch();
    }

    @Test
    void fillCacheAsync_shouldAddHashesToCache() {
        List<String> mockHashes = List.of("hash4", "hash5");
        when(hashRepository.getHashBatch(anyInt())).thenReturn(mockHashes);

        hashCache.fillCacheAsync();

        verify(hashRepository, atLeastOnce()).getHashBatch(anyInt());
        assertTrue(mockHashes.contains(hashCache.getHash()));
    }
}