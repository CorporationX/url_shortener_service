package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.dto.UrlDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlCacheRepositoryTest {

    @InjectMocks
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;

    private String url = "url";
    private String hash = "hash";

    @Test
    void testSave() {
        UrlDto urlDto = new UrlDto(hash, url);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(urlDto.getUrl(), urlDto.getHash(), 0, TimeUnit.SECONDS);
        doNothing().when(valueOperations).set(urlDto.getHash(), urlDto.getUrl(), 0, TimeUnit.SECONDS);

        urlCacheRepository.save(urlDto);

        verify(redisTemplate, times(2)).opsForValue();
        verify(valueOperations, times(1)).set(urlDto.getUrl(), urlDto.getHash(), 0, TimeUnit.SECONDS);
        verify(valueOperations, times(1)).set(urlDto.getHash(), urlDto.getUrl(), 0, TimeUnit.SECONDS);
    }

    @Test
    void testGetHashByUrl() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(url)).thenReturn(hash);

        String result = urlCacheRepository.getHashByUrl(url);

        assertEquals(hash, result);
    }

    @Test
    void testGetHashWhenException() {
        when(redisTemplate.opsForValue()).thenThrow(RedisConnectionFailureException.class);

        String result = urlCacheRepository.getHashByUrl(url);

        assertNull(result);
    }

    @Test
    void testGetUrlByHash() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(hash)).thenReturn(url);

        String result = urlCacheRepository.getUrlByHash(hash);

        assertEquals(url, result);
    }

    @Test
    void testGetUrlByHashWhenException() {
        when(redisTemplate.opsForValue()).thenThrow(RedisConnectionFailureException.class);

        String result = urlCacheRepository.getUrlByHash(hash);

        assertNull(result);
    }
}