package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.properties.short_url.HashProperties;
import faang.school.urlshortenerservice.util.hash_generator.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {

    private static final int queueCapacity = 10;

    @Mock
    private ExecutorService executorService;

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private HashProperties hashProperties;

    private HashCache hashCache;

    @BeforeEach
    void setUp() {
        when(hashProperties.getCacheCapacity()).thenReturn(queueCapacity);
        hashCache = new HashCache(executorService, hashGenerator, hashProperties);
    }

    @Test
    void initCacheTest() throws NoSuchFieldException, IllegalAccessException {
        List<String> hashes = List.of("hash1", "hash2", "hash3");
        when(hashGenerator.getHashes(queueCapacity)).thenReturn(hashes);

        hashCache.initCache();

        Queue<String> cacheQueue = getHashCacheQueue();

        assertFalse(cacheQueue.isEmpty());
        assertEquals(hashes.size(), cacheQueue.size());
        verify(hashGenerator, times(1)).getHashes(queueCapacity);
    }

    @Test
    void getHashWithoutFillingTest() {
        int minPercentageThreshold = 10;
        Queue<String> cacheQueue = getHashCacheQueue();
        cacheQueue.add("Ju");
        cacheQueue.add("W7E");
        when(hashProperties.getMinPercentageThreshold()).thenReturn(minPercentageThreshold);

        String freeHash = hashCache.getHash();

        assertNotNull(freeHash);
        assertEquals("Ju", freeHash);
        assertEquals("W7E", cacheQueue.peek());
        verify(executorService, never()).submit(any(Runnable.class));
    }

    @Test
    void testNeedToFillQueue() {
        int minPercentageThreshold = 30;
        Queue<String> cacheQueue = getHashCacheQueue();
        cacheQueue.add("Ju");
        cacheQueue.add("W7E");
        when(hashGenerator.getHashes(anyLong())).thenReturn(new ArrayList<>());
        when(hashProperties.getMinPercentageThreshold()).thenReturn(minPercentageThreshold);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        String freeHash = hashCache.getHash();

        verify(executorService, times(1)).submit(runnableCaptor.capture());
        runnableCaptor.getValue().run();

        assertNotNull(freeHash);
        assertEquals("Ju", freeHash);
        verify(hashGenerator, times(1)).getHashes(anyLong());
        verify(hashGenerator, times(1)).generateBatch();
    }

    private Queue<String> getHashCacheQueue() {
        try {
            Class<HashCache> clazz = HashCache.class;
            Field queueField = clazz.getDeclaredField("freeHashesQueue");
            queueField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Queue<String> queue = (Queue<String>) queueField.get(hashCache);
            return queue;
        } catch (Exception e) {
            return null;
        }
    }
}