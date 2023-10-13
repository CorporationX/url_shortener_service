package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {

    @Mock
    private HashGenerator generator;
    @Mock
    private HashRepository repository;
    @Mock
    private Executor hashCacheThreadPool;
    @InjectMocks
    private HashCache hashCache;
    private BlockingQueue<String> cache;
    private int cacheSize;
    private int minFill;
    private long cachePollTimeout;
    private Lock lock = new ReentrantLock();

    @BeforeEach
    public void setUp() {
        cacheSize = 10;
        minFill = 20;
        cachePollTimeout = 100;
        cache = new ArrayBlockingQueue<>(cacheSize);
        hashCache.setCachePollTimeout(cachePollTimeout);
        hashCache.setCacheSize(cacheSize);
        hashCache.setMinFill(minFill);
        hashCache.setCache(cache);
    }

    @Test
    void testGetHash() {
        String expected = "1";
        cache.add(expected);
        String actual = hashCache.getHash();
        verify(hashCacheThreadPool).execute(any());
        assertEquals(expected, actual);
    }

    @Test
    void testFillCache() {
        hashCache.fillCache();
        verify(repository).getHashBatch(cacheSize - cache.size());
        verify(generator).generateBatch();
    }

    @Test
    void testAsyncFilling() {
        var executor = Executors.newFixedThreadPool(2);
        Runnable runnable = () -> {
            hashCache.fillCache();
            try {
                executor.awaitTermination(0, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
        executor.execute(runnable);
        executor.execute(runnable);
        executor.shutdown();

        verify(repository, times(1)).getHashBatch(cacheSize - cache.size());
        verify(generator, times(1)).generateBatch();
    }

    @Test
    void testAddHash() {
        int expected = cache.size() + 1;
        String hash = "*";
        hashCache.addHash(hash);
        int actual = cache.size();
        assertEquals(expected, actual);
        assertTrue(cache.contains(hash));
    }
}