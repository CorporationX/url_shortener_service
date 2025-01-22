package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlCacheRepositoryTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private UrlCacheRepository urlCacheRepository;

    @Test
    void saveToCache_shouldSaveUrlToRedis() {
        Url url = new Url("hash123", "https://example.com");
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        urlCacheRepository.saveToCache(url);

        verify(valueOperations).set(url.getHash(), url.getUrl());
    }

    @Test
    void getUrlByHash_shouldReturnUrlFromCache() {
        String hash = "hash123";
        String expectedUrl = "https://example.com";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(hash)).thenReturn(expectedUrl);

        String actualUrl = urlCacheRepository.getUrlByHash(hash);

        assertEquals(expectedUrl, actualUrl);
        verify(valueOperations).get(hash);
        verifyNoInteractions(urlRepository);
    }

    @Test
    void getUrlByHash_shouldReturnUrlFromDatabaseAndSaveToCache() {
        String hash = "hash123";
        String expectedUrl = "https://example.com";
        Url urlEntity = new Url(hash, expectedUrl);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(hash)).thenReturn(null);
        when(urlRepository.findByHash(hash)).thenReturn(Optional.of(urlEntity));

        String actualUrl = urlCacheRepository.getUrlByHash(hash);

        assertEquals(expectedUrl, actualUrl);
        verify(valueOperations).get(hash);
        verify(urlRepository).findByHash(hash);
        verify(valueOperations).set(hash, expectedUrl); // Убедиться, что значение сохранилось в Redis
    }

    @Test
    void getUrlByHash_shouldThrowExceptionWhenUrlNotFoundInDatabase() {
        String hash = "hash123";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(hash)).thenReturn(null);
        when(urlRepository.findByHash(hash)).thenReturn(Optional.empty());

        UrlNotFoundException exception = assertThrows(UrlNotFoundException.class, () ->
                urlCacheRepository.getUrlByHash(hash)
        );

        assertEquals("Url not found by hash: " + hash, exception.getMessage());
        verify(valueOperations).get(hash);
        verify(urlRepository).findByHash(hash);
    }
}