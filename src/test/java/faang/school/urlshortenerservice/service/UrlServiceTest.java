package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.EntityNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    private static final String URL_START = "http://short.url/";
    private static final String LONG_URL = "http://example.com/very/long/url";
    private static final String HASH = "hash1";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "urlStart", URL_START);
    }

    @Test
    void createShortUrl_ReturnShortenedUrl() {
        when(hashCache.getHash()).thenReturn(HASH);
        String result = urlService.createShortUrl(LONG_URL);
        assertEquals(URL_START + HASH, result);
        verify(hashCache).getHash();
        verify(urlRepository).save(any(Url.class));
        verify(urlCacheRepository).save(eq(HASH), eq(LONG_URL));
    }

    @Test
    void getLongUrl_WhenHashIsCached_ReturnLongUrl() {
        when(urlCacheRepository.get(HASH)).thenReturn(LONG_URL);
        String result = urlService.getLongUrl(HASH);
        assertEquals(LONG_URL, result);
        verify(urlCacheRepository).get(HASH);
        verifyNoMoreInteractions(urlRepository);
    }

    @Test
    void getLongUrl_WhenHashIsNotCachedButFoundInRepo_ReturnLongUrl() {
        when(urlCacheRepository.get(HASH)).thenReturn(null);
        when(urlRepository.findByHash(HASH)).thenReturn(new Url(HASH, LONG_URL, LocalDateTime.now()));
        String result = urlService.getLongUrl(HASH);
        assertEquals(LONG_URL, result);
        verify(urlCacheRepository).get(HASH);
        verify(urlRepository).findByHash(HASH);
    }

    @Test
    void getLongUrl_WhenHashIsNotFound_ThrowException() {
        when(urlCacheRepository.get(HASH)).thenReturn(null);
        when(urlRepository.findByHash(HASH)).thenReturn(null);
        assertThrows(EntityNotFoundException.class, () -> urlService.getLongUrl(HASH));
        verify(urlCacheRepository).get(HASH);
        verify(urlRepository).findByHash(HASH);
    }
}
