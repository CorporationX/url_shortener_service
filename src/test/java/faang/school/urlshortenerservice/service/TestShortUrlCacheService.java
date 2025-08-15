package faang.school.urlshortenerservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestShortUrlCacheService {
    private static final String CODE = "test";
    private static final String URL = "https://example.com/page";
    private static final int TTL = 100;
    private static final String PREFIX = "urls:";

    @InjectMocks
    private ShortUrlCacheServiceImpl cacheService;

    @Mock
    private StringRedisTemplate template;
    @Mock
    private ValueOperations<String, String> valueOps;

    @BeforeEach
    void setUp() {
        when(template.opsForValue()).thenReturn(valueOps);
        ReflectionTestUtils.setField(cacheService, "ttl", TTL);
        ReflectionTestUtils.setField(cacheService, "cachePrefix", PREFIX);
    }

    @Test
    void testGetCacheValue() {
        when(valueOps.get(CODE)).thenReturn(URL);

        String actual = cacheService.get(CODE);

        assertEquals(URL, actual);
        verify(template, times(1)).opsForValue();
        verify(valueOps, times(1)).get(CODE);
        verifyNoMoreInteractions(template, valueOps);
    }

    @Test
    void testSetCacheValue() {
        cacheService.set(CODE, URL);

        verify(template, times(1)).opsForValue();
        verify(valueOps, times(1)).set(PREFIX + CODE, URL, TTL, TimeUnit.SECONDS);
        verifyNoMoreInteractions(template, valueOps);
    }
}
