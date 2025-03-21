package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.hashes.HashCache;
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
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    @InjectMocks
    private UrlService urlService;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private HashCache hashCache;

    @Test
    public void getOriginalUrl_urlNotFoundInCache() {
        String hash = "hash";
        when(urlCacheRepository.get(hash)).thenReturn(Optional.empty());

        UrlNotFoundException urlNotFoundException =
                assertThrows(UrlNotFoundException.class, () -> urlService.getOriginalUrl(hash));
        assertEquals(urlNotFoundException.getMessage(), "Url was not found in cache redis");

        verify(urlCacheRepository, times(1)).get(hash);
    }

    @Test
    public void getOriginalUrl_urlNotFound() {
        String hash = "hash";
        String ex = "  ";
        when(urlCacheRepository.get(hash)).thenReturn(Optional.of(ex));
        lenient().when(urlRepository.findUrlByHash(hash)).thenReturn(Optional.empty());

        UrlNotFoundException urlNotFoundException =
                assertThrows(UrlNotFoundException.class, () -> urlService.getOriginalUrl(hash));
        assertEquals(urlNotFoundException.getMessage(), "Url was not found in dataSource");

        verify(urlCacheRepository, times(1)).get(hash);
        verify(urlRepository, times(1)).findUrlByHash(hash);
    }

    @Test
    public void getOriginalUrl_urlFoundInCache() {
        String hash = "hash";
        String url = "http://localhost:8080";
        when(urlCacheRepository.get(hash)).thenReturn(Optional.of(url));

        String originalUrl = urlService.getOriginalUrl(hash);
        assertEquals(originalUrl, url);
        verify(urlCacheRepository, times(1)).get(hash);
    }

    @Test
    public void getOriginalUrl_urlFoundInDataSource() {
        String hash = "hash";
        String hashInCache = " ";
        String url = "http://localhost:8080";
        when(urlCacheRepository.get(hash)).thenReturn(Optional.of(hashInCache));
        when(urlRepository.findUrlByHash(hash)).thenReturn(Optional.of(url));

        String originalUrl = urlService.getOriginalUrl(hash);
        assertEquals(originalUrl, url);
        verify(urlCacheRepository, times(1)).get(hash);
        verify(urlRepository, times(1)).findUrlByHash(hash);
    }
}
