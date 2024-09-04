package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cashe.HashCache;
import faang.school.urlshortenerservice.exception.DuplicateUrlException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
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
    private HashCache hashCache;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @InjectMocks
    private UrlService urlService;

    private static final String LONG_URL = "https://www.example.com";
    private static final String SHORT_HASH = "abc123";
    private static final String SHORT_URL = "http://short.url/" + SHORT_HASH;


    @Test
    void shouldShortUrlNewUrlSuccess() {
        when(urlRepository.existsByUrl(LONG_URL)).thenReturn(false);
        when(hashCache.getHash()).thenReturn(SHORT_HASH);

        String result = urlService.createShortUrl(LONG_URL);

        assertEquals(SHORT_URL, result);
        verify(urlRepository).save(any(Url.class));
        verify(urlCacheRepository).save(any(Url.class));
    }

    @Test
    void shouldShortUrlExistingUrlThrowsException() {
        when(urlRepository.existsByUrl(LONG_URL)).thenReturn(true);
        assertThrows(DuplicateUrlException.class, () -> urlService.createShortUrl(LONG_URL));
    }

    @Test
    void getLongUrlCacheHitReturnsUrl() {
        when(urlCacheRepository.findLongUrlByHash(SHORT_HASH)).thenReturn(Optional.of(LONG_URL));
        String result = urlService.getLongUrl(SHORT_HASH);
        assertEquals(LONG_URL, result);
        verify(urlRepository, never()).findByHash(any());
    }

    @Test
    void getLongUrlCacheMissDbHitReturnsUrlAndCaches() {
        when(urlCacheRepository.findLongUrlByHash(SHORT_HASH)).thenReturn(Optional.empty());
        when(urlRepository.findByHash(SHORT_HASH)).thenReturn(Optional.of(new Url(SHORT_HASH, LONG_URL, null)));
        String result = urlService.getLongUrl(SHORT_HASH);
        assertEquals(LONG_URL, result);
        verify(urlCacheRepository).save(any(Url.class));
    }

    @Test
    void getLongUrlNotFoundThrowsException() {
        when(urlCacheRepository.findLongUrlByHash(SHORT_HASH)).thenReturn(Optional.empty());
        when(urlRepository.findByHash(SHORT_HASH)).thenReturn(Optional.empty());
        assertThrows(UrlNotFoundException.class, () -> urlService.getLongUrl(SHORT_HASH));
    }
}