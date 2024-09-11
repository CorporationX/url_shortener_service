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
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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

//        List<String> initialBatch = List.of("hash1", "hash2", "hash3");
//        when(hashGenerator.getHashBatch(anyInt())).thenReturn(initialBatch);
//
//        CompletableFuture<List<String>> futureBatch = CompletableFuture.completedFuture(List.of("hash4", "hash5"));
//        when(hashGenerator.getHashBatchAsync(anyInt())).thenReturn(futureBatch);
        hashCache.init();
    }

    @Test
    public void testFillInitialBatchWithEmptyBatch() {
        when(hashGenerator.getHashBatch(anyInt())).thenReturn(Collections.emptyList());

        CompletableFuture<List<String>> futureHashes = CompletableFuture.completedFuture(Collections.singletonList("hash1"));
        when(hashGenerator.getHashBatchAsync(anyInt())).thenReturn(futureHashes);

        hashCache.init();

        assertEquals("hash1", hashCache.getHash());
    }

//    @Test
//    @DisplayName("getHashWithoutAdding")
//    void testWithoutAdding() {
//        List<String> initialBatch = List.of("hash1", "hash2", "hash3");
//        when(hashGenerator.getHashBatch(anyInt())).thenReturn(initialBatch);
//        hashCache.init();
//
//        assertEquals("hash1", hashCache.getHash());
//    }

//    @Test
//    @DisplayName("getHashBatchAsync")
//    void testGetHashBatchAsync() {
//        List<String> initialBatch = List.of("hash1", "hash2");
//        when(hashGenerator.getHashBatch(anyInt())).thenReturn(initialBatch);
//
//        CompletableFuture<List<String>> futureBatch = CompletableFuture.completedFuture(List.of("hash4", "hash5"));
//        when(hashGenerator.getHashBatchAsync(anyInt())).thenReturn(futureBatch);
//
//        hashCache.init();
//        hashCache.getHash();
//
////        when(hashGenerator.getHashBatchAsync(anyInt()))
////                .thenReturn(CompletableFuture.completedFuture(Arrays.asList("hash4", "hash5")));
////
//        hashCache.getHash();
//
//        assertEquals("hash4", hashCache.getHash());
//
//        verify(hashGenerator, times(1)).getHashBatchAsync(anyInt());
//    }

    @Test
    @DisplayName("ExceptionDuringHashBatchRetrieval")
    void testExceptionDuringHashBatchRetrieval() {
        when(hashGenerator.getHashBatchAsync(anyInt()))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("exception")));

        hashCache.init();

        String result = hashCache.getHash();

        assertNull(result);
    }

//    @Test
//    @DisplayName("EmptyHashBatch")
//    void testEmptyHashBatch() {
//        // Настраиваем мок для возвращения пустого списка через синхронный метод
//        when(hashGenerator.getHashBatch(anyInt())).thenThrow(new RuntimeException("exception"));
//
//        // Инициализируем кеш
//        hashCache.init();
//
//        // Проверяем, что getHash возвращает null, так как батч пустой и ничего не добавляется
//        assertNull(hashCache.getHash());
//    }
}