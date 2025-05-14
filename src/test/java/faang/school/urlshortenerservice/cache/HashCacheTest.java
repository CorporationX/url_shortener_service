package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.properties.HashProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private Executor executor;

    @Mock
    private HashProperties hashProperties;

    @Mock
    private HashProperties.Cache cacheProps;

    @InjectMocks
    private HashCache hashCache;

    @BeforeEach
    void setup() {
        when(hashProperties.getCache()).thenReturn(cacheProps);
        when(cacheProps.getMaxSize()).thenReturn(10);

    }

    @Test
    void cacheIsNotEmpty() {
        hashCache = new HashCache(hashRepository, hashGenerator, hashProperties, executor);
        var field = getField("cache", hashCache);
        field.addAll(List.of("abc123"));

        String result = hashCache.getHash();

        assertEquals("abc123", result);
    }

    @Test
    void cacheIsEmpty() {
        hashCache = new HashCache(hashRepository, hashGenerator, hashProperties, executor);

        assertThrows(IllegalStateException.class, () -> hashCache.getHash());
    }

    @Test
    void belowThreshold() {
        hashCache = new HashCache(hashRepository, hashGenerator, hashProperties, executor);

        getField("cache", hashCache).add("abc123");

        hashCache.getHash();

        getField("cache", hashCache).clear();

        assertThrows(IllegalStateException.class, () -> hashCache.getHash());

        verify(executor, atLeastOnce()).execute(any(Runnable.class));
    }

    @Test
    void batchEmpty() throws Exception {
        hashCache = new HashCache(hashRepository, hashGenerator, hashProperties, executor);

        when(hashRepository.getHashBatch(anyInt())).thenReturn(List.of());

        var method = HashCache.class.getDeclaredMethod("refillCacheAsync");
        method.setAccessible(true);
        method.invoke(hashCache);

        verify(hashGenerator).generateBatch();
    }

    @SuppressWarnings("unchecked")
    private static Queue<String> getField(String name, HashCache cache) {
        try {
            var field = HashCache.class.getDeclaredField(name);
            field.setAccessible(true);
            return (Queue<String>) field.get(cache);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
