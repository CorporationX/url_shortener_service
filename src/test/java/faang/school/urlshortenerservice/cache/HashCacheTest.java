package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
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
    @InjectMocks
    private HashCache hashCache;
    private BlockingQueue<String> cache;
    private int CACHE_SIZE;
    private int MIN_FILL;
    private Lock lock = new ReentrantLock();

    @BeforeEach
    public void setUp() {
        CACHE_SIZE = 10;
        MIN_FILL = 20;
        cache = new ArrayBlockingQueue<>(CACHE_SIZE);
        hashCache.setCACHE_SIZE(CACHE_SIZE);
        hashCache.setMIN_FILL(MIN_FILL);
        hashCache.setCache(cache);
    }

    @Test
    void testGetHash() {
        String expected = "1";
        cache.add(expected);
        String actual = hashCache.getHash();
        verify(repository).getHashBatch(9L);
        verify(generator).generateBatch();
        assertEquals(expected, actual);
    }

    @Test
    void testFillCache() {
        hashCache.fillCache();
        verify(repository).getHashBatch(CACHE_SIZE - cache.size());
        verify(generator).generateBatch();
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