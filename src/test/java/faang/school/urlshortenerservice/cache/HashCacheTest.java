package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.TaskExecutor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HashCacheTest {

    @InjectMocks
    private HashCache hashCache;

    @Mock
    private TaskExecutor taskThreadPool;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private HashGenerator hashGenerator;

    private static final int QUEUE_SIZE = 10;
    private static final int PERCENTAGE_MULTIPLIER = 20;

    @BeforeEach
    void setUp() throws Exception {
        Field freeCachesField = HashCache.class.getDeclaredField("freeCaches");
        freeCachesField.setAccessible(true);
        freeCachesField.set(hashCache, new ArrayBlockingQueue<>(QUEUE_SIZE));

        Field queueSizeField = HashCache.class.getDeclaredField("queueSize");
        queueSizeField.setAccessible(true);
        queueSizeField.setInt(hashCache, QUEUE_SIZE);

        Field percentageMultiplierField = HashCache.class.getDeclaredField("PERCENTAGE_MULTIPLIER");
        percentageMultiplierField.setAccessible(true);
        percentageMultiplierField.setInt(hashCache, PERCENTAGE_MULTIPLIER);
    }

    @Test
    void refillCache_ShouldAddHashesToCache() throws Exception {
        // Arrange
        when(hashRepository.getHashBatch(anyInt())).thenReturn(Arrays.asList("hash1", "hash2"));

        // Use reflection to invoke the private method
        Method refillCacheMethod = HashCache.class.getDeclaredMethod("refillCache");
        refillCacheMethod.setAccessible(true);

        // Act
        refillCacheMethod.invoke(hashCache);

        // Assert
        assertEquals(2, getCacheSize());
        assertTrue(containsHash("hash1"));
        assertTrue(containsHash("hash2"));
    }

    @Test
    void generateAdditionalHashes_ShouldCallHashGenerator() throws Exception {
        // Use reflection to invoke the private method
        Method generateAdditionalHashesMethod = HashCache.class.getDeclaredMethod("generateAdditionalHashes");
        generateAdditionalHashesMethod.setAccessible(true);

        // Act
        generateAdditionalHashesMethod.invoke(hashCache);

        // Assert
        verify(hashGenerator, times(1)).generateBatch();
    }

    // Helper methods to interact with the private cache
    private int getCacheSize() {
        try {
            Field freeCachesField = HashCache.class.getDeclaredField("freeCaches");
            freeCachesField.setAccessible(true);
            ArrayBlockingQueue<String> cache = (ArrayBlockingQueue<String>) freeCachesField.get(hashCache);
            return cache.size();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean containsHash(String hash) {
        try {
            Field freeCachesField = HashCache.class.getDeclaredField("freeCaches");
            freeCachesField.setAccessible(true);
            ArrayBlockingQueue<String> cache = (ArrayBlockingQueue<String>) freeCachesField.get(hashCache);
            return cache.contains(hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
