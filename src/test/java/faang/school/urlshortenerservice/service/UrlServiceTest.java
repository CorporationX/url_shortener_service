package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.cache.UrlCacheRepository;
import faang.school.urlshortenerservice.entity.UrlEntity;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        ReflectionTestUtils.setField(urlService, "domain", "http://localhost");
    }

    @Test
    void createShortUrlShouldReturnShortUrlWhenCalledWithValidLongUrl() {
        String longUrl = "https://example.com/some/very/long/url";
        String hash = "abc123";

        when(hashCache.getHash()).thenReturn(hash);

        String shortUrl = urlService.createShortUrl(longUrl);

        assertEquals("http://localhost/abc123", shortUrl);

        verify(hashCache, times(1)).getHash();
        verify(urlRepository, times(1)).save(any(UrlEntity.class));
        verify(urlCacheRepository, times(1)).saveUrl(hash, longUrl);
    }

    @Test
    void createShortUrlShouldCallRepositoryAndCacheWhenCalledWithValidLongUrl() {
        String longUrl = "https://example.com/some/long/url";
        String hash = "def456";

        when(hashCache.getHash()).thenReturn(hash);

        urlService.createShortUrl(longUrl);

        verify(urlRepository, times(1)).save(argThat(entity ->
                entity.getHash().equals(hash) &&
                        entity.getUrl().equals(longUrl) &&
                        entity.getCreatedAt() != null
        ));

        verify(urlCacheRepository, times(1)).saveUrl(hash, longUrl);
    }

    @Test
    void testGetOriginalUrlFromCache() {
        String hash = "abc123";
        String expectedUrl = "http://example.com";

        when(urlCacheRepository.getUrl(hash)).thenReturn(expectedUrl);

        String result = urlService.getOriginalUrl(hash);

        assertEquals(expectedUrl, result);

        verify(urlRepository, never()).findById(hash);
    }

    @Test
    void testGetOriginalUrlFromDatabase() {
        String hash = "abc123";
        String expectedUrl = "http://example.com";

        when(urlCacheRepository.getUrl(hash)).thenReturn(null);

        when(urlRepository.findById(hash)).thenReturn(Optional.of(UrlEntity.builder().url(expectedUrl).build()));

        String result = urlService.getOriginalUrl(hash);

        assertEquals(expectedUrl, result);

        verify(urlCacheRepository, times(1)).getUrl(hash);
        verify(urlRepository, times(1)).findById(hash);
    }

    @Test
    void testGetOriginalUrlNotFound() {
        String hash = "abc123";

        when(urlCacheRepository.getUrl(hash)).thenReturn(null);

        when(urlRepository.findById(hash)).thenReturn(java.util.Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> urlService.getOriginalUrl(hash));

        verify(urlCacheRepository, times(1)).getUrl(hash);
        verify(urlRepository, times(1)).findById(hash);
    }
}
