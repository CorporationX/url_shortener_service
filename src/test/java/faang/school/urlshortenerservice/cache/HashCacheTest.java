package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.exception.CacheUpdateException;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class HashCacheTest {

    @InjectMocks
    private HashCache hashCache;

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private ThreadPoolTaskExecutor taskExecutor;

    @Mock
    private BlockingQueue<Hash> mockCache;


    @BeforeEach
    void setUp() {
        mockCache = new ArrayBlockingQueue<>(10);
        ReflectionTestUtils.setField(hashCache, "caches", mockCache);

        ReflectionTestUtils.setField(hashCache, "queueCapacity", 100);
        ReflectionTestUtils.setField(hashCache, "percent", 0.2);
        ReflectionTestUtils.setField(hashCache, "redisBatchSize", 10);
    }

    @Test
    void testCacheIsThreadSafe() throws Exception {
        Hash hash1 = new Hash("threadSafeHash1");
        Hash hash2 = new Hash("threadSafeHash2");
        mockCache.add(hash1);
        mockCache.add(hash2);

        CompletableFuture<Hash> future1 = CompletableFuture.supplyAsync(() -> hashCache.getHash());
        CompletableFuture<Hash> future2 = CompletableFuture.supplyAsync(() -> hashCache.getHash());

        Hash result1 = future1.get();
        Hash result2 = future2.get();


        assertNotEquals(result1, result2);
        assertEquals(0, mockCache.size());
    }


    @Test
    void testGetHash_WhenCacheIsFull() {
        for (int i = 0; i < 10; i++) {
            mockCache.add(new Hash("testHash" + i));
        }

        Hash result = hashCache.getHash();

        assertNotNull(result);

        assertEquals(9, mockCache.size());
    }


    @Test
    void testCacheUpdateThrowsCacheUpdateExceptionWhenErrorOccurs() {
        doThrow(new CacheUpdateException("Error updating cache", new RuntimeException())).when(taskExecutor).execute(any(Runnable.class));

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> hashCache.getHash());

        Throwable throwable = assertThrows(CompletionException.class, future::join).getCause();
        assertInstanceOf(CacheUpdateException.class, throwable);
    }
}
