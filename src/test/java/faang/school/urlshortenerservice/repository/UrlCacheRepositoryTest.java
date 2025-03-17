package faang.school.urlshortenerservice.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlCacheRepositoryTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private UrlCacheRepository urlCacheRepository;

    @Value("${date.ttl.hour.url}")
    private int ttlHours;

    private final String longUrl = "https://example.com";
    private final String hash = "abc123";

    @BeforeEach
    void setUp() {
        Mockito.lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testSaveUrlToCache() {
        Duration ttl = Duration.ofHours(ttlHours);
        urlCacheRepository.save(hash, longUrl);

        verify(valueOperations, times(1)).set("url:" + hash, longUrl, ttl);
    }

    @Test
    void testFindUrlInCache() {
        when(valueOperations.get("url:" + hash)).thenReturn(longUrl);

        Optional<String> result = urlCacheRepository.findByHash(hash);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(longUrl);
    }

    @Test
    void testDeleteUrlFromCache() {
        urlCacheRepository.delete(hash);
        verify(redisTemplate, times(1)).delete("url:" + hash);
    }
}