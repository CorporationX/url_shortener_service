package faang.school.urlshortenerservice.cashe;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.RedisTemplate;

@ExtendWith(MockitoExtension.class)
class ShortUrlCacheTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private ShortUrlCache shortUrlCache;

    private String hash;
    private String longUrl;
    private String notExistentHash;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        hash = "abc123";
        longUrl = "https://url_shortner.com";
        notExistentHash = "APPLE123";
    }

    @Test
    void testSaveUrl() {
        shortUrlCache.saveUrl(hash, longUrl);
        verify(valueOperations, times(1)).set(hash, longUrl);
    }

    @Test
    void testGetUrl() {
        when(valueOperations.get(hash)).thenReturn(longUrl);
        String result = shortUrlCache.getUrl(hash);
        assertEquals(longUrl, result);
        verify(valueOperations, times(1)).get(hash);
    }

    @Test
    void testGetUrl_NotFound() {
        when(valueOperations.get(notExistentHash)).thenReturn(null);
        String result = shortUrlCache.getUrl(notExistentHash);
        assertNull(result);
        verify(valueOperations, times(1)).get(notExistentHash);
    }
}
