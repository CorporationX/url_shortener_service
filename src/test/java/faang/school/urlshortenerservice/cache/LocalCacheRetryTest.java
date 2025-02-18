package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.exception.CacheEmptyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class LocalCacheRetryTest {

    @InjectMocks
    private LocalCacheRetry localCache;

    int capacityTest = 4;

    @Test
    void getCachedHashSuccessTest() {
        String testHash = "a1b";
        Queue<String> hashes = new ArrayBlockingQueue<>(capacityTest);
        hashes.add(testHash);
        assertEquals(testHash, localCache.getCachedHash(hashes), "The hash is not equal.");
    }

    @Test
    void getCachedHashNoElementInQueueExceptionFailTest() {
        Queue<String> hashes = new ArrayBlockingQueue<>(capacityTest);
        String message = "There are a lot requests. Please, try again later.";

        Exception exception = assertThrows(CacheEmptyException.class, () -> localCache.getCachedHash(hashes));

        assertEquals(message, exception.getMessage(), "The massage is not equal.");
    }
}