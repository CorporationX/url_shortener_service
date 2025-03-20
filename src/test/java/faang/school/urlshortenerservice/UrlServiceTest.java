package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.redis.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.HashCache;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private HashCache hashCache;

    @InjectMocks
    private UrlService urlService;

    @BeforeEach
    void setUp() {
        urlService = new UrlService(urlRepository, urlCacheRepository, hashCache);
        lenient().when(hashCache.getHash()).thenReturn("abc123");
    }

    @Test
    void testCreateShortUrl_Success() {
        String longUrl = "https://example.com";
        when(urlRepository.save(any(Url.class))).thenReturn(new Url("abc123", longUrl, null));

        String shortUrl = urlService.createShortUrl(longUrl);

        assertTrue(shortUrl.contains("abc123"));
        verify(urlRepository).save(any(Url.class));
        verify(urlCacheRepository).save("abc123", longUrl);
    }

    @Test
    void testGetOriginalUrl_FromCache() {
        String hash = "abc123";
        String longUrl = "https://example.com";
        when(urlCacheRepository.findByHash(hash)).thenReturn(Optional.of(longUrl));

        String result = urlService.getOriginalUrl(hash);

        assertEquals(longUrl, result);
        verify(urlCacheRepository).findByHash(hash);
        verify(urlRepository, never()).findById(hash);
    }

    @Test
    void testGetOriginalUrl_FromDb() {
        String hash = "abc123";
        String longUrl = "https://example.com";
        Url urlEntity = new Url(hash, longUrl, null);
        when(urlCacheRepository.findByHash(hash)).thenReturn(Optional.empty());
        when(urlRepository.findById(hash)).thenReturn(Optional.of(urlEntity));

        String result = urlService.getOriginalUrl(hash);

        assertEquals(longUrl, result);
        verify(urlCacheRepository).save(hash, longUrl);
        verify(urlRepository).findById(hash);
    }

    @Test
    void testGetOriginalUrl_NotFound() {
        String hash = "abc123";
        when(urlCacheRepository.findByHash(hash)).thenReturn(Optional.empty());
        when(urlRepository.findById(hash)).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> urlService.getOriginalUrl(hash));
    }
}
