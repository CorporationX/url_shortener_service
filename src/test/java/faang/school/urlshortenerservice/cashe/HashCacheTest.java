package faang.school.urlshortenerservice.cashe;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.generator.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {

    private static final int CACHE_SIZE = 10;
    private static final double THRESHOLD = 0.5;

    @Mock
    private HashGenerator hashGenerator;

    @InjectMocks
    private HashCache hashCache;

    private ExecutorService executorService;
    private Queue<String> hashQueue;

    @BeforeEach
    void setUp() {
        executorService = Executors.newSingleThreadExecutor();
        ReflectionTestUtils.setField(hashCache, "hashExecutor", executorService);
        ReflectionTestUtils.setField(hashCache, "cacheSize", CACHE_SIZE);
        ReflectionTestUtils.setField(hashCache, "threshold", THRESHOLD);
        hashQueue = new ConcurrentLinkedQueue<>();
        ReflectionTestUtils.setField(hashCache, "hashQueue", hashQueue);
    }

    @Test
    void TestInit_CacheWithHashes() {
        List<Hash> hashes = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Hash hash = new Hash();
            hash.setHash("hash" + i);
            hashes.add(hash);
        }
        when(hashGenerator.getHashes()).thenReturn(hashes);

        hashCache.init();

        assertEquals(5, hashQueue.size());
        for (int i = 0; i < 5; i++) {
            assertTrue(hashQueue.contains("hash" + i));
        }
    }

    @Test
    void TestInit_HandleException() {
        when(hashGenerator.getHashes()).thenThrow(new RuntimeException("Test exception"));

        assertThrows(IllegalStateException.class, () -> hashCache.init());
    }

    @Test
    void TestGetHash_ReturnHashFromQueue() {
        hashQueue.offer("hash1");

        String hash = hashCache.getHash();

        assertEquals("hash1", hash);
        assertEquals(0, hashQueue.size());
    }

    @Test
    void TestGetHash_QueueIsEmpty() {
        List<Hash> newHashes = new ArrayList<>();
        Hash hash = new Hash();
        hash.setHash("newHash");
        newHashes.add(hash);

        when(hashGenerator.getHashes()).thenReturn(newHashes);

        String result = hashCache.getHash();

        assertEquals("newHash", result);
        assertEquals(0, hashQueue.size());
    }

    @Test
    void TestGetHash_QueueLowUseAsync() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            hashQueue.offer("hash" + i);
        }

        List<Hash> newHashes = new ArrayList<>();
        for (int i = 5; i < 10; i++) {
            Hash hash = new Hash();
            hash.setHash("hash" + i);
            newHashes.add(hash);
        }
        when(hashGenerator.getHashes()).thenReturn(newHashes);
        hashCache.getHash();

        Thread.sleep(100);
        assertEquals(9, hashQueue.size());
    }
}