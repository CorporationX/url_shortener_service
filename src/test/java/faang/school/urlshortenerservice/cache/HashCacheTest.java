package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.service.HashGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {
    @Mock
    private HashGenerator hashGenerator;

    @InjectMocks
    private HashCache hashCache;

    @Test
    void getHashShouldReturnHashWhenCacheIsNotEmpty() throws NoSuchFieldException, IllegalAccessException {
        BlockingQueue<String> cache = new LinkedBlockingQueue<>();
        cache.offer("hash123");
        var cacheField = HashCache.class.getDeclaredField("cache");
        cacheField.setAccessible(true);
        cacheField.set(hashCache, cache);

        String retrievedHash = hashCache.getHash();

        assertEquals("hash123", retrievedHash);
    }

    @Test
    void getHashShouldRefillCacheWhenCacheIsEmpty() {
        List<String> generatedHashes = List.of("hash1", "hash2", "hash3");
        when(hashGenerator.generateBatch(anyInt())).thenReturn(generatedHashes);

        String retrievedHash = hashCache.getHash();

        assertTrue(generatedHashes.contains(retrievedHash));
        verify(hashGenerator, times(1)).generateBatch(anyInt());
    }

    @Test
    void getHashShouldThrowExceptionWhenInterrupted() throws Exception {
        BlockingQueue<String> cache = mock(BlockingQueue.class);
        when(cache.take()).thenThrow(new InterruptedException());

        var cacheField = HashCache.class.getDeclaredField("cache");
        cacheField.setAccessible(true);
        cacheField.set(hashCache, cache);

        RuntimeException exception = assertThrows(RuntimeException.class, hashCache::getHash);
        assertEquals("Не удалось получить хэш из кэша", exception.getMessage());
    }

    @Test
    void refillCacheIfNeededShouldAddHashesToCacheWhenBelowThreshold() throws Exception {
        List<String> generatedHashes = List.of("hash1", "hash2", "hash3");
        when(hashGenerator.generateBatch(anyInt())).thenReturn(generatedHashes);

        var refillMethod = HashCache.class.getDeclaredMethod("refillCacheIfNeeded");
        refillMethod.setAccessible(true);
        refillMethod.invoke(hashCache);

        var cacheField = HashCache.class.getDeclaredField("cache");
        cacheField.setAccessible(true);
        BlockingQueue<String> cache = (BlockingQueue<String>) cacheField.get(hashCache);

        assertEquals(3, cache.size());
        verify(hashGenerator, times(1)).generateBatch(anyInt());
    }
}
