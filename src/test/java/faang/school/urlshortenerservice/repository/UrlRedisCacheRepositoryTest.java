package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.config.redis.RedisProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlRedisCacheRepositoryTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private UrlRedisCacheRepository urlRedisCacheRepository;

    private RedisProperties redisProperties;
    private String hash;
    private String longUrl;

    @BeforeEach
    void setUp() {
        hash = "HASHHH";
        longUrl = "http://longUrl";
        redisProperties = new RedisProperties("host", 123, 123);
        urlRedisCacheRepository = new UrlRedisCacheRepository(redisProperties, redisTemplate);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testSaveUrlSuccess() {
        urlRedisCacheRepository.saveUrl(hash, longUrl);

        verify(valueOperations, times(1)).set(hash, longUrl, redisProperties.ttl(), TimeUnit.SECONDS);
    }

    @Test
    void testGetUrlReturnsValue() {
        when(valueOperations.get(hash)).thenReturn(longUrl);

        Optional<String> result = urlRedisCacheRepository.findByHash(hash);

        verify(valueOperations, times(1)).get(hash);
        assertThat(result.get()).isEqualTo(longUrl);
    }

    @Test
    void testGetUrlReturnsEmptyOptional() {
        when(valueOperations.get(hash)).thenReturn(null);

        Optional<String> result = urlRedisCacheRepository.findByHash(hash);

        verify(valueOperations, times(1)).get(hash);
        assertThat(result.isEmpty()).isTrue();
    }
}