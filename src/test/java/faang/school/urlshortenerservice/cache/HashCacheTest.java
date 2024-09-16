package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {

    @Mock
    private HashGenerator hashGenerator;
    @InjectMocks
    private HashCache hashCache;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashCache, "capacity", 10);
        ReflectionTestUtils.setField(hashCache, "minPercentage", 0.50);
    }

    @Test
    @DisplayName("initValid")
    void testInitValid() {
        when(hashGenerator.getHashBatch(anyInt()))
                .thenReturn(Arrays.asList("hash1", "hash2", "hash3", "hash4", "hash5", "hash6"));

        hashCache.init();

        verify(hashGenerator, times(1)).getHashBatch(anyInt());
    }

    @Test
    @DisplayName("initException")
    void testInitException() {
        when(hashGenerator.getHashBatch(anyInt()))
                .thenThrow(new RuntimeException("exception"));

        Exception exception =  assertThrows(RuntimeException.class, () ->
                hashCache.init());

        assertEquals("exception", exception.getMessage());
    }

    @Test
    @DisplayName("getHashValid")
    void testGetHashValid() {
        List<String> hashes = Arrays.asList("hash1", "hash2", "hash3", "hash4", "hash5", "hash6");
        when(hashGenerator.getHashBatch(anyInt())).thenReturn(hashes);

        hashCache.init();

        assertEquals("hash1", hashCache.getHash());
    }

    @Test
    public void testFillInitialBatchWithEmptyBatch() {
        when(hashGenerator.getHashBatch(anyInt())).thenReturn(Collections.emptyList());

        CompletableFuture<List<String>> futureHashes = CompletableFuture.completedFuture(Collections.singletonList("hash1"));
        when(hashGenerator.getHashBatchAsync(anyInt())).thenReturn(futureHashes);

        hashCache.init();

        assertEquals("hash1", hashCache.getHash());
    }

    @Test
    @DisplayName("ExceptionDuringHashBatchRetrieval")
    void testExceptionDuringHashBatchRetrieval() {
        when(hashGenerator.getHashBatchAsync(anyInt()))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("exception")));

        hashCache.init();

        String result = hashCache.getHash();

        assertNull(result);
    }
}