package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {

    @Mock
    private HashGenerator hashGenerator;

    private HashCache hashCache;
    private final int CAPACITY = 100;
    private final int MIN_PERCENTAGE = 30;
    private BlockingQueue<String> testQueue;

    @BeforeEach
    void setUp() {
        hashCache = new HashCache(CAPACITY, MIN_PERCENTAGE, hashGenerator);
        testQueue = new ArrayBlockingQueue<>(CAPACITY);
        try {
            var field = HashCache.class.getDeclaredField("hashes");
            field.setAccessible(true);
            field.set(hashCache, testQueue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testInit_FillsCacheOnStartup() {
        List<String> initialHashes = List.of("hash1", "hash2", "hash3");
        when(hashGenerator.getHashes(CAPACITY)).thenReturn(initialHashes);

        hashCache.init();

        assertEquals(initialHashes.size(), testQueue.size());
        assertTrue(testQueue.containsAll(initialHashes));
    }

    @Test
    void testGetHash_DoesNotTriggerRefillWhenAboveThreshold() throws InterruptedException {
        List<String> hashes = generateHashes(31);
        testQueue.addAll(hashes);

        hashCache.getHash();

        verify(hashGenerator, never()).getHashesAsync(anyInt());
    }

    @Test
    void testGetHash_BlocksWhenEmpty() {
        Thread gettingThread = new Thread(() -> {
            try {
                hashCache.getHash();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        gettingThread.start();
        assertTrue(gettingThread.isAlive());

        testQueue.add("unblock");
        try {
            gettingThread.join(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertFalse(gettingThread.isAlive());
    }

    @Test
    void testConcurrentAccess_ThreadSafety() throws InterruptedException {
        // Fill cache
        testQueue.addAll(generateHashes(CAPACITY));

        int threadCount = 10;
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                try {
                    hashCache.getHash();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join(1000);
        }

        assertEquals(CAPACITY - threadCount, testQueue.size());
    }

    private List<String> generateHashes(int count) {
        return java.util.stream.IntStream.range(0, count)
                .mapToObj(i -> "hash" + i)
                .toList();
    }
}