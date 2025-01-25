package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HashCacheTest {

    private HashCache hashCache;
    private HashRepository hashRepository;
    private HashGenerator hashGenerator;

    @BeforeEach
    void setUp() {
        hashRepository = mock(HashRepository.class);
        hashGenerator = mock(HashGenerator.class);
        hashCache = new HashCache(hashRepository, hashGenerator);

        ReflectionTestUtils.setField(hashCache, "maxCacheSize", 100);
        ReflectionTestUtils.setField(hashCache, "thresholdPercentage", 20);
    }

    @Test
    void testRefreshCacheAddsHashes() {
        when(hashRepository.getHashBatch(100)).thenReturn(List.of("hashA", "hashB", "hashC"));

        ReflectionTestUtils.invokeMethod(hashCache, "refreshCache");

        ConcurrentLinkedQueue<String> cache = (ConcurrentLinkedQueue<String>) ReflectionTestUtils.getField(hashCache, "cache");
        assertEquals(3, cache.size(), "Expected cache size to be 3");
        assertEquals("hashA", cache.poll(), "Expected first hash to be hashA");
    }

    @Test
    void testRefreshCacheTriggersHashGeneratorWhenRepositoryEmpty() {
        when(hashRepository.getHashBatch(anyInt())).thenReturn(List.of());

        ReflectionTestUtils.invokeMethod(hashCache, "refreshCache");

        verify(hashGenerator, times(1)).generateBatch();
    }

    @Test
    void testRefreshCacheLogsCorrectly() {
        when(hashRepository.getHashBatch(anyInt())).thenReturn(List.of("hashX", "hashY"));

        hashCache.getHash();

        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(hashRepository).getHashBatch(captor.capture());
        assertEquals(100, captor.getValue(), "Expected hash batch size to be 100");
    }
}
