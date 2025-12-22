package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @InjectMocks
    private UrlService urlService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "baseUrl", "http://short.url");
    }

    @Test
    void testCreateShortUrl() {
        String longUrl = "https://example.com/very/long/url";
        String hash = "abc123";

        when(hashCache.getHash()).thenReturn(hash);

        String result = urlService.createShortUrl(longUrl);

        assertEquals("http://short.url/abc123", result);
        verify(urlRepository).save(hash, longUrl);
        verify(urlCacheRepository).save(hash, longUrl);
    }

    @Test
    void testGetOriginalUrlFromCache() {
        String hash = "abc123";
        String url = "https://example.com";

        when(urlCacheRepository.findByHash(hash)).thenReturn(Optional.of(url));

        String result = urlService.getOriginalUrl(hash);

        assertEquals(url, result);
        verify(urlCacheRepository).findByHash(hash);
        verify(urlRepository, never()).findByHash(anyString());
    }

    @Test
    void testGetOriginalUrlFromDatabase() {
        String hash = "abc123";
        String url = "https://example.com";

        when(urlCacheRepository.findByHash(hash)).thenReturn(Optional.empty());
        when(urlRepository.findByHash(hash)).thenReturn(Optional.of(url));

        String result = urlService.getOriginalUrl(hash);

        assertEquals(url, result);
        verify(urlCacheRepository).findByHash(hash);
        verify(urlRepository).findByHash(hash);
        verify(urlCacheRepository).save(hash, url);
    }

    @Test
    void testGetOriginalUrlNotFound() {
        String hash = "notfound";

        when(urlCacheRepository.findByHash(hash)).thenReturn(Optional.empty());
        when(urlRepository.findByHash(hash)).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> urlService.getOriginalUrl(hash));
    }
}