package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.сache.CacheProperties;
import faang.school.urlshortenerservice.service.hashCache.HashCache;
import faang.school.urlshortenerservice.service.hashGenerator.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {

    private HashGenerator hashGenerator;
    private CacheProperties cacheProperties;
    private HashCache hashCache;

    @BeforeEach
    void setUp() {
        hashGenerator = Mockito.mock(HashGenerator.class);
        cacheProperties = Mockito.mock(CacheProperties.class);

        when(cacheProperties.getCapacity()).thenReturn(10);
        when(hashGenerator.getHashes(anyLong())).thenReturn(List.of("hash1", "hash2", "hash3", "hash4", "hash5", "hash6", "hash7", "hash8", "hash9", "hash10"));
        when(cacheProperties.getMinLimitCapacity()).thenReturn(20);

        hashCache = new HashCache(cacheProperties, hashGenerator);
    }

    @Test
    void testGetHash_ShouldReturnHashFromQueue() {
        String hash = hashCache.getHash();
        assertEquals("hash1", hash);
    }

    @Test
    void testGetHash_WhenBelowLimit_ShouldTriggerAsyncFill() throws InterruptedException {
        when(hashGenerator.getHashesAsync(anyLong())).
                thenReturn(CompletableFuture.completedFuture(List.of("hash11", "hash12")));

        for (int i = 0; i < 10; i++) {
            hashCache.getHash();
        }
        Thread.sleep(100);
        verify(hashGenerator).getHashesAsync(anyLong());
    }

    @Test
    void testGetHash_WhenNotBelowLimit_ShouldNotTriggerAsyncFill() {
        hashCache.getHash();

        verify(hashGenerator, never()).getHashesAsync(anyInt());
    }
}
