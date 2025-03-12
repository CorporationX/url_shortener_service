package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlCacheTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private UrlCache urlCache;

    private static final String HASH = "abc123";
    private static final String ORIGINAL_URL = "https://example.com";
    private static final String URL_KEY = "url:abc123";
    private static final String COUNTER_KEY = "counter:abc123";
    private static final int MIN_REQUESTS = 10;
    private static final int CACHE_TTL_HOURS = 24;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlCache, "minRequestsForCaching", MIN_REQUESTS);
        ReflectionTestUtils.setField(urlCache, "cacheTtlHours", CACHE_TTL_HOURS);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void getOriginalUrl_ShouldReturnFromCache_WhenUrlIsCached() {
        when(valueOperations.get(URL_KEY)).thenReturn(ORIGINAL_URL);
        when(valueOperations.increment(COUNTER_KEY)).thenReturn(1L);

        String result = urlCache.getOriginalUrl(HASH);

        assertThat(result).isEqualTo(ORIGINAL_URL);
        verify(valueOperations).get(URL_KEY);
        verify(redisTemplate).expire(COUNTER_KEY, CACHE_TTL_HOURS, TimeUnit.HOURS);
    }

    @Test
    void getOriginalUrl_ShouldFetchFromRepository_WhenNotCached() {
        when(valueOperations.get(URL_KEY)).thenReturn(null);
        when(valueOperations.increment(COUNTER_KEY)).thenReturn(1L);
        when(urlRepository.findByHash(HASH)).thenReturn(Optional.of(
            Url.builder().hash(HASH).originalUrl(ORIGINAL_URL).build()
        ));

        String result = urlCache.getOriginalUrl(HASH);

        assertThat(result).isEqualTo(ORIGINAL_URL);
        verify(valueOperations).get(URL_KEY);
        verify(urlRepository).findByHash(HASH);
        verify(redisTemplate).expire(COUNTER_KEY, CACHE_TTL_HOURS, TimeUnit.HOURS);
    }

    @Test
    void getOriginalUrl_ShouldCacheUrl_WhenRequestCountExceedsThreshold() {
        when(valueOperations.get(URL_KEY)).thenReturn(null);
        when(valueOperations.increment(COUNTER_KEY)).thenReturn((long) MIN_REQUESTS);
        when(urlRepository.findByHash(HASH)).thenReturn(Optional.of(
            Url.builder().hash(HASH).originalUrl(ORIGINAL_URL).build()
        ));

        String result = urlCache.getOriginalUrl(HASH);

        assertThat(result).isEqualTo(ORIGINAL_URL);
        verify(valueOperations).set(URL_KEY, ORIGINAL_URL, CACHE_TTL_HOURS, TimeUnit.HOURS);
    }

    @Test
    void getOriginalUrl_ShouldThrowException_WhenUrlNotFound() {
        when(valueOperations.get(URL_KEY)).thenReturn(null);
        when(valueOperations.increment(COUNTER_KEY)).thenReturn(1L);
        when(urlRepository.findByHash(HASH)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> urlCache.getOriginalUrl(HASH))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("Url not found");
    }

    @Test
    void getOriginalUrl_ShouldHandleRedisFailure() {
        when(valueOperations.increment(COUNTER_KEY)).thenReturn(null);
        when(urlRepository.findByHash(HASH)).thenReturn(Optional.of(
            Url.builder().hash(HASH).originalUrl(ORIGINAL_URL).build()
        ));

        String result = urlCache.getOriginalUrl(HASH);

        assertThat(result).isEqualTo(ORIGINAL_URL);
        verify(urlRepository).findByHash(HASH);
    }
} 