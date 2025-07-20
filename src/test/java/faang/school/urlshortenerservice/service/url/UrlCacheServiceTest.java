package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.entity.Url;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlCacheServiceTest {
    @InjectMocks
    private UrlCacheService urlCacheService;

    @Mock
    private RedisTemplate<String, Url> redis;

    @Mock
    private ValueOperations<String, Url> valueOps;

    private static final long ttlDays = 7L;

    @BeforeEach
    void setUp() {
        when(redis.opsForValue()).thenReturn(valueOps);
        ReflectionTestUtils.setField(urlCacheService, "ttl", 7L);
    }

    @Test
    void testSaveUrl() {
        Url url = new Url("abc123", "https://example.com", null);

        urlCacheService.saveUrl("abc123", url);

        verify(valueOps).set("abc123", url, ttlDays, TimeUnit.DAYS);
    }

    @Test
    void testGetUrl() {
        Url expected = new Url("abc123", "https://example.com", null);
        when(valueOps.get("abc123")).thenReturn(expected);

        Url result = urlCacheService.getUrl("abc123");

        assertEquals(expected, result);
    }
}