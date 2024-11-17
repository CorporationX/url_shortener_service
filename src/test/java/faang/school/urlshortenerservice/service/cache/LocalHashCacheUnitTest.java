package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.service.HashGenerator;
import faang.school.urlshortenerservice.service.HashService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LocalHashCacheUnitTest {

    @Mock
    HashService hashService;

    @Mock
    HashGenerator hashGenerator;

    @Mock
    Executor hashGeneratorExecutor;

    LocalHashCache localHashCache;
    Queue<String> cache;

    @BeforeEach
    void init() throws NoSuchFieldException, IllegalAccessException {
        int testCapacity = 8;
        double testThreshold = 0.2;

        localHashCache = spy(new LocalHashCache(
                hashService,
                hashGenerator,
                testCapacity,
                testThreshold,
                hashGeneratorExecutor
        ));

        Field cacheField = LocalHashCache.class.getDeclaredField("cache");
        cacheField.setAccessible(true);

        cache = (ArrayBlockingQueue<String>) cacheField.get(localHashCache);
    }

    @Test
    void testFillCache() {
        localHashCache.fillCache();
        verify(localHashCache).updateLocalCache();
    }

    @Test
    void testGetHash() {
        cache.add("hash1");
        cache.add("hash2");

        var hash = localHashCache.getHash();

        assertEquals("hash1", hash);
        verify(hashGeneratorExecutor).execute(any(Runnable.class));
    }

    @Test
    void testUpdateLocalCache() {
        when(hashService.getAndDeleteHashBatch()).thenReturn(List.of("hash1", "hash2", "hash3"));

        localHashCache.updateLocalCache();

        assertEquals(3, cache.size());
        assertTrue(cache.contains("hash1"));
        assertTrue(cache.contains("hash2"));
        assertTrue(cache.contains("hash3"));

        verify(hashGenerator).generateBatch();
    }
}
