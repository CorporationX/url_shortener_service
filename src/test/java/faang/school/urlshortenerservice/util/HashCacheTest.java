package faang.school.urlshortenerservice.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {
    private static final int CACHE_CAPACITY = 3;
    private static final int THRESHOLD = 2;
    private static final int BATCH_SIZE = 2;
    private static final int INITIAL_MIN_SIZE = 6;
    private static final int INITIAL_FILLING_SIZE = 3;

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private ExecutorService fillUpCacheExecutorService;

    private HashCache hashCache;

    private void initWithGenerateBatchOfHashes() {
        when(hashGenerator.getHashesCount()).thenReturn(0);
        when(hashGenerator.getHashes(anyInt())).thenReturn(List.of("hash1", "hash2", "hash3"));
        hashCache = new HashCache(CACHE_CAPACITY, THRESHOLD, BATCH_SIZE, INITIAL_MIN_SIZE, INITIAL_FILLING_SIZE,
                hashGenerator, fillUpCacheExecutorService);
        verify(hashGenerator).generateBatchOfHashes(eq(CACHE_CAPACITY + BATCH_SIZE));
    }

    private void initWithoutGenerateBatchOfHashes() {
        when(hashGenerator.getHashesCount()).thenReturn(INITIAL_MIN_SIZE);
        hashCache = new HashCache(CACHE_CAPACITY, THRESHOLD, BATCH_SIZE, INITIAL_MIN_SIZE, INITIAL_FILLING_SIZE,
                hashGenerator, fillUpCacheExecutorService);
        verify(hashGenerator, never()).generateBatchOfHashes(anyInt());
    }

    @Test
    void testConstructor_GetExistedHashes() {
        initWithoutGenerateBatchOfHashes();
    }

    @Test
    void testGetHash_RunFillUpCache() {
        initWithGenerateBatchOfHashes();

        String hash1 = hashCache.getHash();
        assertEquals("hash1", hash1);
        verify(fillUpCacheExecutorService, never()).execute(any(Runnable.class));

        String hash2 = hashCache.getHash();
        assertEquals("hash2", hash2);
        verify(fillUpCacheExecutorService, never()).execute(any(Runnable.class));

        when(hashGenerator.getHashes(eq(BATCH_SIZE))).thenReturn(List.of("hash4", "hash5"));

        String hash3 = hashCache.getHash();
        assertEquals("hash3", hash3);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(fillUpCacheExecutorService).execute(runnableCaptor.capture());
        Runnable fillUpTask = runnableCaptor.getValue();
        fillUpTask.run();

        verify(fillUpCacheExecutorService).execute(any(Runnable.class));
        verify(hashGenerator).getHashes(BATCH_SIZE);
        verify(hashGenerator).generateBatchOfHashesAsync(CACHE_CAPACITY);
    }

    @Test
    void testGetHash_SkipFillUpCache() {
        initWithGenerateBatchOfHashes();

        String hash1 = hashCache.getHash();
        assertEquals("hash1", hash1);
        verify(fillUpCacheExecutorService, never()).execute(any(Runnable.class));

        String hash2 = hashCache.getHash();
        assertEquals("hash2", hash2);
        verify(fillUpCacheExecutorService, never()).execute(any(Runnable.class));

        ReflectionTestUtils.setField(hashCache, "isFillingUp", new AtomicBoolean(true));

        String hash3 = hashCache.getHash();
        assertEquals("hash3", hash3);

        verify(fillUpCacheExecutorService, never()).execute(any(Runnable.class));
    }
}