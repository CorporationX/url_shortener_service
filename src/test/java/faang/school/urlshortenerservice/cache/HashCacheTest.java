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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.when;

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
        hashCache.init();
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