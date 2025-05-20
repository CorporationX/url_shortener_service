package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.HashGenerator.HashGenerator;
import faang.school.urlshortenerservice.chache.HashCache;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashCacheTest {

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private HashRepository repository;

    private HashCache hashCache;

    private final int cacheSize = 100;
    private final int fillPercent = 20;

    @BeforeEach
    void setUp() {
        hashCache = new HashCache(repository, hashGenerator, cacheSize, fillPercent);
    }

    @Test
    void positiveGetHashFromRepo() {
        List<String> generatedHashes = List.of("hash1", "hash2", "hash3");
        when(repository.getHashBatch(cacheSize)).thenReturn(generatedHashes);

        hashCache.prepareCache();

        Queue<String> hashes = (Queue<String>) ReflectionTestUtils.getField(hashCache, "hashes");
        assertEquals(hashes.size(), hashes.size());
        assertTrue(hashes.containsAll(hashes));
    }

    @Test
    void positiveGetHashFromQueue() {
        Queue<String> testQueue = new ArrayBlockingQueue<>(cacheSize);
        testQueue.add("hash1");
        testQueue.add("hash2");
        ReflectionTestUtils.setField(hashCache, "hashes", testQueue);

        CompletableFuture<String> result = hashCache.getHash();

        assertEquals("hash1", result.join());
        assertTrue(testQueue.contains("hash2"));
    }

    @Test
    public void positiveTriggeredByBelowMinimum() {
        Queue<String> testQueue = new ArrayBlockingQueue<>(cacheSize);
        for (int i = 0; i < 19; i++) {
            testQueue.add("hash" + i);
        }
        ReflectionTestUtils.setField(hashCache, "hashes", testQueue);

        when(repository.getHashBatch(81)).thenReturn(List.of("hash1", "hash2"));

        hashCache.getHash();

        verify(repository, times(1)).getHashBatch(81);
        verify(hashGenerator, times(1)).generateBatch();
    }

    @Test
    public void positiveNoNeedRefill() {
        Queue<String> testQueue = new ArrayBlockingQueue<>(cacheSize);
        for (int i = 0; i < 21; i++) {
            testQueue.add("h" + i);
        }
        ReflectionTestUtils.setField(hashCache, "hashes", testQueue);

        hashCache.getHash();

        verifyNoInteractions(repository);
        verifyNoInteractions(hashGenerator);
        assertEquals(20, testQueue.size());

    }


}
