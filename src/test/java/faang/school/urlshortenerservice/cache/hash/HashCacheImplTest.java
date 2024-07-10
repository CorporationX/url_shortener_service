package faang.school.urlshortenerservice.cache.hash;

import faang.school.urlshortenerservice.service.generator.HashGenerator;
import faang.school.urlshortenerservice.service.generator.async.AsyncHashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashCacheImplTest {

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private AsyncHashGenerator asyncHashGenerator;

    private HashCacheImpl hashCache;

    @BeforeEach
    void setUp() {
        int capacity = 5;
        double minCapacityPercents = 10;
        hashCache = new HashCacheImpl(capacity, minCapacityPercents, hashGenerator, asyncHashGenerator);

        when(hashGenerator.getBatch()).thenReturn(Arrays.asList("hash1", "hash2", "hash3"));
        hashCache.init();
    }

    @Test
    void pop_whenQueueIsAboveMinCapacity_returnsHash() {
        String result = hashCache.pop();
        assertNotNull(result);
        assertTrue(Arrays.asList("hash1", "hash2", "hash3").contains(result));
    }

    @Test
    void pop_whenQueueIsBelowMinCapacity_triggersAsyncGeneration() {

        hashCache.pop();
        hashCache.pop();
        hashCache.pop();

        when(asyncHashGenerator.getBatchAsync()).thenReturn(CompletableFuture.completedFuture(Arrays.asList("hash4", "hash5")));

        String result = hashCache.pop();

        InOrder inOrder = inOrder(asyncHashGenerator, hashGenerator);
        inOrder.verify(asyncHashGenerator, times(1)).getBatchAsync();
        assertNotNull(result);
    }

    @Test
    void putAll_addsAllHashesToQueue() {
        hashCache.putAll(Arrays.asList("hash4", "hash5"));

        assertTrue(hashCache.pop().startsWith("hash"));
    }
}