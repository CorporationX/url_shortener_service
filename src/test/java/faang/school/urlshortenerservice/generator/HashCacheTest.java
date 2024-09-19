package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.TaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashCacheTest {
    @Mock
    private HashGenerator hashGenerator;
    @Mock
    private HashRepository hashRepository;
    @Mock
    private TaskExecutor hashCacheExecutor;
    @InjectMocks
    private HashCache hashCache;
    private int capacity = 3;
    private int fillPercent = 100;
    private BlockingQueue<String> queue = new LinkedBlockingQueue<>(capacity);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashCache, "capacity", capacity);
        ReflectionTestUtils.setField(hashCache, "fillPercent", fillPercent);
        ReflectionTestUtils.setField(hashCache, "hashes", queue);

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(hashCacheExecutor).execute(any(Runnable.class));

        queue.addAll(List.of("hash1", "hash2", "hash3"));
    }

    @Test
    public void testGetHashWithFullCache() {
        String result = hashCache.getHash();

        verify(hashRepository, times(0)).getHashBatch(anyInt());

        assertEquals("hash1", result);
        assertEquals(2, queue.size());
    }

    @Test
    public void testGetHashWithRefillCache() {
        hashCache.getHash();
        when(hashRepository.getHashBatch(1)).thenReturn(List.of("hash1"));
        String result = hashCache.getHash();

        verify(hashRepository, times(1)).getHashBatch(anyInt());
        verify(hashGenerator, times(0)).generateBatchAsync();

        assertEquals("hash2", result);
        assertEquals(2, queue.size());
    }

    @Test
    public void testGetHashWithRefillCacheWithGenerateHashes() {
        hashCache.getHash();
        when(hashRepository.getHashBatch(1))
                .thenReturn(List.of())
                        .thenReturn(List.of("hash1"));
        when(hashGenerator.generateBatchAsync()).thenReturn(CompletableFuture.completedFuture(null));
        String result = hashCache.getHash();

        verify(hashRepository, times(2)).getHashBatch(anyInt());
        verify(hashGenerator, times(1)).generateBatchAsync();

        assertEquals("hash2", result);
        assertEquals(2, queue.size());
    }
}
