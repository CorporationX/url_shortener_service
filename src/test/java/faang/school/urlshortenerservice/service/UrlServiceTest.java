package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.InvalidUrlException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.Optional;

import static faang.school.urlshortenerservice.message.ErrorMessage.INVALID_URL;
import static faang.school.urlshortenerservice.message.ErrorMessage.URL_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {
    private static final String ORIGINAL_URL = "http://example.com";
    private static final String HASH = "abc123";

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashCache hashCache;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private UrlServiceImpl urlService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "baseUrl", "http://urls/");
        ReflectionTestUtils.setField(urlService, "urlTtl", 24);
    }

    @Test
    void createShortUrl_shouldReturnShortShortUrl() {
        when(hashCache.getHash()).thenReturn(HASH);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UrlResponseDto result = urlService.createShortUrl(ORIGINAL_URL);

        assertEquals("http://urls/abc123", result.getUrl());
        verify(urlRepository).save(argThat(url ->
                url.getUrl().equals(ORIGINAL_URL) &&
                        url.getHash().equals(HASH) &&
                        url.getCreatedAt() != null
        ));
        verify(redisTemplate.opsForValue()).set(eq(HASH), eq(ORIGINAL_URL), eq(Duration.ofHours(24)));
    }

    @Test
    void createShortUrl_shouldThrowInvalidUrlException_whenShortUrlIsMalformed() {
        String invalidUrl = "example";

        InvalidUrlException e = assertThrows(InvalidUrlException.class,
                () -> urlService.createShortUrl(invalidUrl));

        assertEquals(INVALID_URL, e.getMessage());
    }

    @Test
    void getOriginalUrl_shouldReturnOriginalUrlFromCache() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(HASH)).thenReturn(ORIGINAL_URL);

        String result = urlService.getOriginalUrl(HASH);

        assertEquals(ORIGINAL_URL, result);
        verify(redisTemplate.opsForValue()).get(HASH);
    }

    @Test
    void getOriginalUrl_shouldReturnOriginalUrlFromDb() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(HASH)).thenReturn(null);
        when(urlRepository.findByHash(HASH)).thenReturn(Optional.of(Url.builder()
                .hash(HASH)
                .url(ORIGINAL_URL)
                .build()));

        String result = urlService.getOriginalUrl(HASH);

        assertEquals(ORIGINAL_URL, result);
        verify(redisTemplate.opsForValue()).get(HASH);
        verify(urlRepository).findByHash(HASH);
    }

    @Test
    void getOriginalUrl_shouldThrowUrlNotFoundException_ifNotExistsInDbAndCache() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(HASH)).thenReturn(null);
        when(urlRepository.findByHash(HASH)).thenReturn(Optional.empty());

        UrlNotFoundException e = assertThrows(UrlNotFoundException.class,
                () -> urlService.getOriginalUrl(HASH));

        assertEquals(URL_NOT_FOUND, e.getMessage());
        verify(redisTemplate.opsForValue()).get(HASH);
        verify(urlRepository).findByHash(HASH);
    }
}
