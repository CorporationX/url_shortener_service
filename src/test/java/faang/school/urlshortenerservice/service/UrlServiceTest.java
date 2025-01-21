package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createShortUrlValidHashSuccessTest() {
        String url = "https://example.com";
        String hash = "abc123";

        when(hashCache.getHash()).thenReturn(hash);

        String result = urlService.createShortUrl(url);

        assertEquals(hash, result);
        verify(hashCache).getHash();
        verify(urlCacheRepository).saveUrl(hash, url);
        verify(urlRepository).save(new Url(hash, url));
    }

    @Test
    void createShortUrlNullHashFailTest() {
        String url = "https://example.com";

        when(hashCache.getHash()).thenReturn(null);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> urlService.createShortUrl(url));
        assertEquals("Failed to generate a hash for the URL.", exception.getMessage());

        verify(hashCache).getHash();
        verifyNoInteractions(urlCacheRepository);
        verifyNoInteractions(urlRepository);
    }

    @Test
    void getUrlByHashFoundInCacheSuccessTest() {
        String hash = "abc123";
        String url = "https://example.com";

        when(urlCacheRepository.findUrlByHash(hash)).thenReturn(url);

        String result = urlService.getUrlByHash(hash);

        assertEquals(url, result);
        verify(urlCacheRepository).findUrlByHash(hash);
        verifyNoInteractions(urlRepository);
    }

    @Test
    void getUrlByHashSuccessTest() {
        String hash = "abc123";
        String url = "https://example.com";
        Url urlEntity = new Url(hash, url);

        when(urlCacheRepository.findUrlByHash(hash)).thenReturn(null);
        when(urlRepository.findByHash(hash)).thenReturn(Optional.of(urlEntity));

        String result = urlService.getUrlByHash(hash);

        assertEquals(url, result);
        verify(urlCacheRepository).findUrlByHash(hash);
        verify(urlRepository).findByHash(hash);
        verify(urlCacheRepository).saveUrl(hash, url);
    }

    @Test
    void getUrlByHashFailTest() {
        String hash = "abc123";

        when(urlCacheRepository.findUrlByHash(hash)).thenReturn(null);
        when(urlRepository.findByHash(hash)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> urlService.getUrlByHash(hash));
        assertEquals("URL not found for hash: abc123", exception.getMessage());

        verify(urlCacheRepository).findUrlByHash(hash);
        verify(urlRepository).findByHash(hash);
    }
}
