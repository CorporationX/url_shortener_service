package faang.school.urlshortenerservice.cache.hash;

import faang.school.urlshortenerservice.generator.hash.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashCacheTest {

    @InjectMocks
    private HashCache hashCache;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private ExecutorService generateBatchPool;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private Set<String> keys;

    @BeforeEach
    public void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        hashCache = new HashCache(redisTemplate, hashGenerator, generateBatchPool);
        ReflectionTestUtils.setField(hashCache, "getBatchSize", 10);
        ReflectionTestUtils.setField(hashCache, "minValueRedis", 0.2);
        ReflectionTestUtils.setField(hashCache, "hashPattern", "hash:");
    }

    @Test
    public void testSaveHashToCache() {
        String pattern = "hash:";
        List<String> values = Arrays.asList("value1", "value2");

        hashCache.saveToCache(pattern, values);

        verify(redisTemplate.opsForValue(), times(1)).set("hash:value1", "value1");
        verify(redisTemplate.opsForValue(), times(1)).set("hash:value2", "value2");
    }

    @Test
    public void testSaveHashPlusValueToCache() {
        String key = "hash:value";
        String value = "value";

        hashCache.saveToCache(key, value);

        verify(redisTemplate.opsForValue(), times(1)).set(key, value);
    }

    @Test
    public void testGetRandomHashFromCache() {
        when(redisTemplate.keys("hash:*")).thenReturn(Collections.emptySet());

        String result = hashCache.getRandomHashFromCache();

        assertNull(result);
    }
}