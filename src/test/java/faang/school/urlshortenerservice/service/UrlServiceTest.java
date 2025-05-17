package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.chache.HashCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.exception.InvalidUrlException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
public class UrlServiceTest {

    @InjectMocks
    private UrlService urlService;

    @Mock
    private HashCache cache;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;


    @Test
    public void positiveFullGetShortUrlLink() {
        String originalUrl = "https://google.com";
        String hash = "abc123";
        String shortUrl = "https://shortener/";
        CompletableFuture<String> hashFuture = CompletableFuture.completedFuture(hash);

        when(cache.getHash()).thenReturn(hashFuture);
        when(urlRepository.save(any())).thenReturn(new Url(hash, originalUrl, LocalDateTime.now()));

        String result = urlService.getShortUrlLink(originalUrl);

        assertNotNull(result);
        verify(urlCacheRepository).save(hash, originalUrl);
    }

    @Test
    public void negativeGetShorLinkInvalidUrl() {
        String wrongUrl = "ss.(@)!@;";
        Url wrongEntity = new Url();
        String hash = "acb";

        assertThrows(InvalidUrlException.class, () ->
            urlService.getShortUrlLink(wrongUrl));

        verify(urlRepository, times(0)).save(wrongEntity);
        verify(urlCacheRepository, times(0)).save(hash, wrongUrl);
    }

    @Test
    public void positiveGetOriginalUrl() {
        String hash = "abc123";
        String cachedUrl = "https://example.com";

        when(urlCacheRepository.findByHash(hash)).thenReturn(cachedUrl);

        String result = urlService.getOriginalUrl(hash);

        assertEquals(cachedUrl, result);
        verify(urlRepository, never()).findById(any());
    }

    @Test
    public void negativeGetOriginalLinkHashIsNull() {
        assertThrows(DataValidationException.class, () -> urlService.getOriginalUrl(" "));
    }

    @Test
    public void negativeGetOriginalLinkUrlNotFound() {
        String hash = "nonexistent";

        when(urlCacheRepository.findByHash(hash)).thenReturn(null);
        when(urlRepository.findById(hash)).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> urlService.getOriginalUrl(hash));
    }
}
