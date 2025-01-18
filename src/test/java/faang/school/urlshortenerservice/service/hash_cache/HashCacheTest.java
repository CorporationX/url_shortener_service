package faang.school.urlshortenerservice.service.hash_cache;

import faang.school.urlshortenerservice.config.async.ThreadPool;
import faang.school.urlshortenerservice.properties.HashCacheQueueProperties;
import faang.school.urlshortenerservice.repository.hash.impl.HashRepositoryImpl;
import faang.school.urlshortenerservice.service.generator.HashGenerator;
import faang.school.urlshortenerservice.util.BatchCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HashCacheTest {

    @Mock
    private HashCacheQueueProperties queueProp;

    @Mock
    private HashRepositoryImpl hashRepository;

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private ThreadPool threadPool;

    @Mock
    private BatchCreator batchCreator;

    @InjectMocks
    private HashCache hashCache;

    @Mock
    private ThreadPoolTaskExecutor hashCacheFillExecutor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(queueProp.getMaxQueueSize()).thenReturn(100);
        when(queueProp.getFillingBatchesQuantity()).thenReturn(5);
        when(queueProp.getPercentageToStartFill()).thenReturn(50);
        when(queueProp.getCountToStopGenerate()).thenReturn(200);

        ReflectionTestUtils.setField(hashCache, "localHashCache", new LinkedBlockingQueue<>(100));
        when(threadPool.hashCacheFillExecutor()).thenReturn(hashCacheFillExecutor);
    }

    @Test
    void fillCacheTest() {
        List<String> mockHashes = Arrays.asList("hash1", "hash2", "hash3", "hash4", "hash5");
        when(hashRepository.getHashBatch(anyInt())).thenReturn(mockHashes);

        List<List<String>> subBatches = Arrays.asList(
                Arrays.asList("hash1", "hash2"),
                Arrays.asList("hash3", "hash4"),
                Collections.singletonList("hash5")
        );
        when(batchCreator.getBatches(mockHashes, queueProp.getFillingBatchesQuantity())).thenReturn(subBatches);
        when(hashRepository.getHashesCount()).thenReturn(150L);

        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(hashCacheFillExecutor).execute(any(Runnable.class));

        CompletableFuture<Void> future = hashCache.fillCache();
        future.join();

        verify(hashRepository, times(1)).getHashBatch(anyInt());
        verify(batchCreator, times(1)).getBatches(mockHashes, queueProp.getFillingBatchesQuantity());
        verify(hashGenerator, times(1)).generateBatchHashes(anyInt());

        Queue<String> localHashCache = (Queue<String>) ReflectionTestUtils.getField(hashCache, "localHashCache");
        assertNotNull(localHashCache);
        assertEquals(5, localHashCache.size());
        assertTrue(localHashCache.containsAll(mockHashes));
    }

    @Test
    void generateHashesTest() {
        when(hashRepository.getHashesCount()).thenReturn(150L);

        ReflectionTestUtils.invokeMethod(hashCache, "generateHashes", 10);
        verify(hashGenerator, times(1)).generateBatchHashes(10);

        when(hashRepository.getHashesCount()).thenReturn(250L);
        ReflectionTestUtils.invokeMethod(hashCache, "generateHashes", 10);
        verify(hashGenerator, times(1)).generateBatchHashes(10);
    }
}
