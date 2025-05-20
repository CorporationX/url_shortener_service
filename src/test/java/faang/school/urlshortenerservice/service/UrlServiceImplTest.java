package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceImplTest {

    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlCacheRepository urlCacheRepository;
    @Mock
    private HashCache hashCache;

    @InjectMocks
    private UrlServiceImpl urlService;

    @Test
    void shouldCreateShortUrlSuccessfully() {
        String longUrl = "http://test.com";
        String hash = "123xyz";

        when(hashCache.getHash()).thenReturn(hash);

        String result = urlService.shortenUrl(longUrl);

        verify(urlRepository).save(any(Url.class));
        verify(urlCacheRepository).save(hash, longUrl);
        assertEquals(hash, result);
    }

    @Test
    void shouldReturnFromCacheWhenExists() {
        String hash = "123xyz";
        String longUrl = "http://test.com";

        when(urlCacheRepository.findOriginalUrlByHash(hash)).thenReturn(Optional.of(longUrl));

        String result = urlService.getOriginalUrl(hash);

        assertEquals(longUrl, result);
    }

    @Test
    void shouldReturnFromDbIfNotInCache() {
        String hash = "123xyz";
        String longUrl = "http://test.com";
        Url url = Url.builder().hash(hash).originalUrl(longUrl).build();

        when(urlCacheRepository.findOriginalUrlByHash(hash)).thenReturn(Optional.empty());
        when(urlRepository.findByHash(hash)).thenReturn(Optional.of(url));

        String result = urlService.getOriginalUrl(hash);

        verify(urlCacheRepository).save(hash, longUrl);
        assertEquals(longUrl, result);
    }

    @Test
    void shouldThrowExceptionIfNotFound() {
        String hash = "notfound";

        when(urlCacheRepository.findOriginalUrlByHash(hash)).thenReturn(Optional.empty());
        when(urlRepository.findByHash(hash)).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> urlService.getOriginalUrl(hash));
    }

    @Test
    void shouldThrowExceptionWhenSaveFails() {
        String longUrl = "http://test.com";
        String hash = "123xyz";

        when(hashCache.getHash()).thenReturn(hash);
        doThrow(new RuntimeException("Еrror")).when(urlRepository).save(any());

        assertThrows(RuntimeException.class, () -> urlService.shortenUrl(longUrl));

        verify(urlCacheRepository, never()).save(anyString(), anyString());
    }
}

