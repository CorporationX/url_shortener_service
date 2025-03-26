package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {
    @Mock
    private HashGenerator hashGenerator;

    @InjectMocks
    private HashCache hashCache;

    private final int capacity = 15;
    private final List<String> mockHashes = List.of("hash1", "hash2", "hash3", "hash4", "hash5");

    @BeforeEach
    void setUp() {
        when(hashGenerator.getHashes(anyInt())).thenReturn(mockHashes);
        hashCache.capacity = capacity;
        hashCache.fillPercent = 27;
    }

    @Test
    void init() {
        hashCache.init();

        verify(hashGenerator).getHashes(capacity);
        assertEquals(mockHashes.size(), hashCache.getHashes().size());
        assertTrue(hashCache.getHashes().containsAll(mockHashes));

    }

    @Test
    void testGetHashWithoutUpdate() {
        hashCache.init();

        String result = hashCache.getHash();

        assertEquals("hash1", result);
        verify(hashGenerator, times(0)).getHashesAsync(capacity);
    }

    @Test
    void testGetHashWithUpdate() {
        when(hashGenerator.getHashesAsync(capacity)).thenReturn(CompletableFuture.completedFuture(mockHashes));
        hashCache.init();
        hashCache.getHashes().poll();

        String result = hashCache.getHash();

        assertEquals("hash2", result);
        verify(hashGenerator).getHashesAsync(capacity);
    }
}