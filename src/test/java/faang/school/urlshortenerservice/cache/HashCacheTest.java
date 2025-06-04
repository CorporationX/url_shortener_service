package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private Executor hashCacheExecutor;

    private HashCache hashCache;

    @BeforeEach
    void setUp() {
        hashCache = new HashCache(hashGenerator, hashCacheExecutor);
        ReflectionTestUtils.setField(hashCache, "capacity", 10);
        ReflectionTestUtils.setField(hashCache, "refillThreshold", 0.2);

        List<String> initial = List.of("h1", "h2", "h3");
        when(hashGenerator.getHashBatch(10)).thenReturn(initial);

        hashCache.init();
    }

    @Test
    void shouldThrowExceptionWhenGeneratorReturnsEmptyList() {
        ReflectionTestUtils.setField(hashCache, "hashes", new ArrayBlockingQueue<>(10));
        when(hashGenerator.getHashBatch(10)).thenReturn(List.of());

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> hashCache.getHash()
        );

        assertTrue(exception.getMessage().contains("Failed to generate hash"));
    }

    @Test
    void shouldNotRefillIfCacheIsFull() {
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(10);
        IntStream.range(0, 10).forEach(i -> queue.add("x" + i));
        ReflectionTestUtils.setField(hashCache, "hashes", queue);

        hashCache.getHash();

        verifyNoMoreInteractions(hashGenerator);
    }

    @Test
    void shouldGenerateHashWhenCacheIsEmpty() {
        ReflectionTestUtils.setField(hashCache, "hashes", new ArrayBlockingQueue<>(10));
        List<String> newHashes = new ArrayList<>(List.of("h1", "h2", "h3"));
        when(hashGenerator.getHashBatch(10)).thenReturn(newHashes);

        String hash = hashCache.getHash();

        assertEquals("h1", hash);
    }

    @Test
    void shouldRefillHashAsynchronously() throws Exception {
        CompletableFuture<Void> future = new CompletableFuture<>();

        Executor testExecutor = command -> {
            command.run();
            future.complete(null);
        };

        hashCache = new HashCache(hashGenerator, testExecutor);
        ReflectionTestUtils.setField(hashCache, "capacity", 5);
        ReflectionTestUtils.setField(hashCache, "refillThreshold", 0.4);
        ReflectionTestUtils.setField(hashCache, "loadingInProgress", new AtomicBoolean(true));

        ArrayBlockingQueue<String> smallQueue = new ArrayBlockingQueue<>(5);
        smallQueue.add("a");
        ReflectionTestUtils.setField(hashCache, "hashes", smallQueue);

        List<String> generated = List.of("b", "c", "d");
        when(hashGenerator.getHashBatch(4)).thenReturn(generated);

        Method refill = HashCache.class.getDeclaredMethod("refillHash");
        refill.setAccessible(true);
        refill.invoke(hashCache);

        future.get(2, TimeUnit.SECONDS);

        BlockingQueue<String> queue = (BlockingQueue<String>) ReflectionTestUtils.getField(hashCache, "hashes");
        assertTrue(queue.contains("b"));
        assertTrue(queue.contains("c"));
        assertTrue(queue.contains("d"));
    }
}