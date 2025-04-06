package faang.school.urlshortenerservice.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisCacheRepositoryTest {

    @InjectMocks
    private RedisCacheRepository redisCacheRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Test
    void getUrl_WhenHashExists_ShouldReturnUrlAndExtendExpiry() {
        // Arrange
        String hash = "abcdef";
        String url = "https://www.example.com";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(hash)).thenReturn(url);

        // Act
        String result = redisCacheRepository.getUrl(hash);

        // Assert
        assertEquals(url, result);
        verify(valueOperations, times(1)).get(hash);
        verify(redisTemplate, times(1)).expire(hash, RedisCacheRepository.duration);
    }

    @Test
    void getUrl_WhenHashDoesNotExist_ShouldReturnNull() {
        // Arrange
        String hash = "abcdef";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(hash)).thenReturn(null);

        // Act
        String result = redisCacheRepository.getUrl(hash);

        // Assert
        assertNull(result);
        verify(valueOperations, times(1)).get(hash);
        verify(redisTemplate, never()).expire(anyString(), any());
    }

    @Test
    void savePair_ShouldSaveUrlInRedis() {
        // Arrange
        String hash = "abcdef";
        String url = "https://www.example.com";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // Act
        redisCacheRepository.savePair(hash, url);

        // Assert
        verify(valueOperations, times(1)).set(hash, url, RedisCacheRepository.duration);
    }
}