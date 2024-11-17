package faang.school.urlshortenerservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {
    @Mock
    private HashGenerator hashGenerator;

    @InjectMocks
    private HashCache hashCache;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashCache, "cacheSize", 10);
        ReflectionTestUtils.setField(hashCache, "threshold", 0.2);
        hashCache.init();
    }

    @Test
    void getHash_shouldReturnHashFromCache() {
        Queue<String> hashQueue = new ArrayBlockingQueue<>(10);
        hashQueue.add("hash1");
        ReflectionTestUtils.setField(hashCache, "hashQueue", hashQueue);

        String hash = hashCache.getHash();
        assertEquals("hash1", hash);
    }

    @Test
    void getHash_shouldTriggerAsyncFillWhenCacheIsLow() throws InterruptedException {
        hashCache.getHash();
        Thread.sleep(100);
        verify(hashGenerator, atLeastOnce()).generateBatch();
    }
}