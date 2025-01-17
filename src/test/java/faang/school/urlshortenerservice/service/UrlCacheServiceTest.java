package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.redis.RedisProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlCacheServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private RedisProperties redisProperties;

    @InjectMocks
    private UrlCacheService urlCacheService;

    String hash;
    String url;

    @BeforeEach
    void setUp() {
        hash = "123abc";
        url = "http:/google.com";
        redisProperties = new RedisProperties(10, "host", 20);
        urlCacheService = new UrlCacheService(redisProperties, redisTemplate);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("Test save to cache success")
    void test_saveToCache_success() {

        urlCacheService.saveToCache(hash, url);

        int timeout = redisProperties.ttl();

        verify(redisTemplate, times(1)).opsForValue();
        verify(valueOperations, times(1)).set(anyString(), anyString());
        verify(redisTemplate, times(1)).expire(hash, Duration.ofSeconds(timeout));
    }

    @Test
    @DisplayName("Test get from cache")
    void test_getFromCache_ValidInput_ReturnsValue() {

        when(valueOperations.get(anyString())).thenReturn(url);

        Optional<String> result = urlCacheService.getFromCache(hash);

        verify(redisTemplate, times(1)).opsForValue();
        verify(valueOperations, times(1)).get(anyString());

        assertNotNull(result);
        assertEquals(url, result.get());
    }

    @Test
    @DisplayName("Test get from cache: key not found")
    void test_getFromCache_InvalidInput_ReturnsEmptyOptional() {

        when(valueOperations.get(hash)).thenReturn(null);

        Optional<String> result = urlCacheService.getFromCache(hash);

        verify(redisTemplate, times(1)).opsForValue();
        verify(valueOperations, times(1)).get(anyString());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}