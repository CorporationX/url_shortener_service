package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.config.properties.url.UrlCacheProperties;
import faang.school.urlshortenerservice.repository.cache.UrlCacheImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlCacheTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOps;

    @Captor
    private ArgumentCaptor<List<String>> keysCaptor;

    private UrlCacheImpl repository;

    @BeforeEach
    public void setUp() {
        UrlCacheProperties props = new UrlCacheProperties("urls:", Duration.ofSeconds(60));
        repository = new UrlCacheImpl(props, redisTemplate);
    }

    @Test
    @DisplayName("Should return null when hash is null or blank")
    public void getReturnsNullWhenHashInvalid() {
        assertNull(repository.get(null));
        assertNull(repository.get(""));
        assertNull(repository.get("   "));
        verify(redisTemplate, never()).opsForValue();
    }

    @Test
    @DisplayName("Should return value from Redis with prefixed key")
    public void getReturnsValueWhenPresent() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get("urls:abc")).thenReturn("https://example.com");
        String result = repository.get("abc");

        assertEquals("https://example.com", result);

        verify(redisTemplate).opsForValue();
        verify(valueOps).get("urls:abc");
        verifyNoMoreInteractions(redisTemplate, valueOps);
    }

    @Test
    @DisplayName("Should skip put when args are invalid")
    public void putSkipsOnInvalidArgs() {
        repository.put(null, "u");
        repository.put("   ", "u");
        repository.put("abc", null);
        repository.put("abc", "");

        verify(redisTemplate, never()).opsForValue();
    }

    @Test
    @DisplayName("Should store value with TTL")
    public void putStoresValueWithTtl() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);

        repository.put("abc", "https://example.com");

        verify(redisTemplate).opsForValue();
        verify(valueOps).set(eq("urls:abc"), eq("https://example.com"), eq(Duration.ofSeconds(60)));
        verifyNoMoreInteractions(redisTemplate, valueOps);
    }

    @Test
    @DisplayName("Should not throw when Redis put fails")
    public void putDoesNotThrowOnRedisError() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        doThrow(new RuntimeException("boom"))
                .when(valueOps).set(anyString(), anyString(), any(Duration.class));

        assertDoesNotThrow(() -> repository.put("abc", "https://example.com"));

        verify(redisTemplate).opsForValue();
        verify(valueOps).set(eq("urls:abc"), eq("https://example.com"), eq(Duration.ofSeconds(60)));
    }

    @Test
    @DisplayName("Should skip delete when list is null or empty")
    public void deleteSkipsOnNullOrEmptyList() {
        repository.delete(null);
        repository.delete(List.of());

        verify(redisTemplate, never()).delete(anyList());
    }

    @Test
    @DisplayName("Should filter invalid hashes and delete prefixed keys")
    public void deleteFiltersInvalidHashesAndDeletes() {
        List<String> input = Arrays.asList("a", " ", null, "b");

        repository.delete(input);

        verify(redisTemplate).delete(keysCaptor.capture());
        List<String> keys = keysCaptor.getValue();

        assertEquals(2, keys.size());
        assertTrue(keys.contains("urls:a"));
        assertTrue(keys.contains("urls:b"));
    }

    @Test
    @DisplayName("Should not throw when Redis delete fails")
    public void deleteDoesNotThrowOnRedisError() {
        doThrow(new RuntimeException("boom")).when(redisTemplate).delete(anyList());

        assertDoesNotThrow(() -> repository.delete(List.of("a", "b")));

        verify(redisTemplate).delete(anyList());
    }
}
