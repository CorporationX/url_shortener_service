package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @InjectMocks
    private UrlService urlService;

    private static final String ORIGINAL_URL = "https://example.com";
    private static final String HASH = "abc123";
    private static final String SHORT_URL = "http://short.url/" + HASH;

    @Test
    void shortenUrl_success() {
        when(hashCache.getHash()).thenReturn(HASH);

        String result = urlService.shortenUrl(ORIGINAL_URL);

        assertEquals(SHORT_URL, result);

        Url expectedUrl = new Url();
        expectedUrl.setHash(HASH);
        expectedUrl.setUrl(ORIGINAL_URL);
        verify(urlRepository).save(expectedUrl);
        verify(urlCacheRepository).save(HASH, ORIGINAL_URL);
        verify(urlCacheRepository).printValue(HASH);
    }

    @Test
    void shortenUrl_hashCacheEmpty_throwsException() {
        when(hashCache.getHash()).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> urlService.shortenUrl(ORIGINAL_URL));
        assertEquals("Failed to generate hash: HashCache is empty", exception.getMessage());

        verify(urlRepository, never()).save(any());
        verify(urlCacheRepository, never()).save(anyString(), anyString());
        verify(urlCacheRepository, never()).printValue(anyString());
    }

    @Test
    void shortenUrl_verifyInteractions() {
        when(hashCache.getHash()).thenReturn(HASH);

        urlService.shortenUrl(ORIGINAL_URL);

        verify(hashCache, times(1)).getHash();
        verify(urlRepository, times(1)).save(any(Url.class));
        verify(urlCacheRepository, times(1)).save(HASH, ORIGINAL_URL);
        verify(urlCacheRepository, times(1)).printValue(HASH);
    }
}