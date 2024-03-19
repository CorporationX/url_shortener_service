package faang.school.urlshortenerservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HashCacheTest {

    @Mock
    private HashGenerator hashGenerator;

    @InjectMocks
    private HashCache hashCache;

    private static final int CAPACITY = 100;
    private static final int FILL_PERCENT = 20;

    @BeforeEach
    public void setUp() {
        List<String> generatedHashes = IntStream.range(0, CAPACITY)
                .mapToObj(i -> "hash" + i)
                .collect(Collectors.toList());

        when(hashGenerator.getHashes(anyInt())).thenReturn(generatedHashes);
        lenient().when(hashGenerator.getHashAsync(anyInt()))
                .thenReturn(CompletableFuture.completedFuture(generatedHashes));

        ReflectionTestUtils.setField(hashCache, "capacity", CAPACITY);
        ReflectionTestUtils.setField(hashCache, "fillPercent", FILL_PERCENT);
        hashCache.init();
    }

    @Test
    public void init_WhenCalled_FillsQueueWithHashes() {
        ArrayBlockingQueue<String> queue = (ArrayBlockingQueue<String>) ReflectionTestUtils.getField(hashCache, "hashes");
        assertNotNull(queue);
        assertFalse(queue.isEmpty());
    }

    @Test
    public void getHash_WhenQueueBelowThreshold_TriggersAsyncFill() {
        for (int i = 0; i <= CAPACITY - (CAPACITY * FILL_PERCENT / 100) + 1; i++) {
            hashCache.getHash();
        }

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> hashCache.getHash());
        CompletableFuture.allOf(future).join();

        verify(hashGenerator, timeout(100)).getHashAsync(anyInt());
    }

    @Test
    public void getHash_WhenQueueAboveThreshold_DoesNotTriggerAsyncFill() {
        hashCache.getHash();

        verify(hashGenerator, never()).getHashAsync(anyInt());
    }
}
