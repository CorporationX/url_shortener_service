package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.NotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.repository.redis.UrlCacheRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlService urlService;

    private final String shortenerUrl = "http://short.url/";

    @BeforeEach
    void setup() throws Exception {
        java.lang.reflect.Field field = urlService.getClass().getDeclaredField("shortenerUrl");
        field.setAccessible(true);
        field.set(urlService, shortenerUrl);
    }

    @Test
    void testGetShortUrlSuccessful() throws Exception {
        String originalUrl = "http://example.com";
        String generatedHash = "abc123";
        UrlDto inputDto = new UrlDto(originalUrl);

        when(hashCache.getHash()).thenReturn(generatedHash);

        UrlDto result = urlService.getShortUrl(inputDto);

        assertEquals(shortenerUrl + generatedHash, result.getUrl());
        verify(urlRepository, times(1)).save(any(Url.class));
        verify(urlCacheRepository, times(1)).save(generatedHash, originalUrl);
    }

    @Test
    void testGetShortUrlInterruptedException() throws Exception {
        String originalUrl = "http://example.com";
        UrlDto inputDto = new UrlDto(originalUrl);
        when(hashCache.getHash()).thenThrow(new InterruptedException("Interrupted"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> urlService.getShortUrl(inputDto));
        assertTrue(exception.getMessage().contains("Failed to generate short URL"));
    }

    @Test
    void testGetLongUrlFromCache() {
        String hash = "abc123";
        String longUrl = "http://example.com";
        when(urlCacheRepository.findByHash(hash)).thenReturn(Optional.of(longUrl));

        String result = urlService.getLongUrl(hash);

        assertEquals(longUrl, result);
        verify(urlCacheRepository, times(1)).findByHash(hash);
        verify(urlRepository, never()).getByHash(anyString());
    }

    @Test
    void testGetLongUrlFromRepository() {
        String hash = "abc123";
        String longUrl = "http://example.com";
        when(urlCacheRepository.findByHash(hash)).thenReturn(Optional.empty());
        when(urlRepository.getByHash(hash)).thenReturn(Optional.of(longUrl));

        String result = urlService.getLongUrl(hash);

        assertEquals(longUrl, result);
        verify(urlCacheRepository, times(1)).findByHash(hash);
        verify(urlRepository, times(1)).getByHash(hash);
    }

    @Test
    void testGetLongUrlNotFound() {
        String hash = "abc123";
        when(urlCacheRepository.findByHash(hash)).thenReturn(Optional.empty());
        when(urlRepository.getByHash(hash)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> urlService.getLongUrl(hash));
        assertTrue(exception.getMessage().contains("cannot be found"));
    }
}
