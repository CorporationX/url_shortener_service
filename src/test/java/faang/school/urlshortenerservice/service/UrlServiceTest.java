package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.domain.DomainConfig;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.dto.ShortenUrlRequest;
import faang.school.urlshortenerservice.model.entity.Url;
import faang.school.urlshortenerservice.repository.cache.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.jpa.UrlRepository;
import faang.school.urlshortenerservice.service.hash.HashCache;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    @Mock
    private HashCache hashCache;

    @Mock
    private DomainConfig domainConfig;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlMapper urlMapper;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @InjectMocks
    private UrlService urlService;

    @Test
    void shortenUrl_ShouldReturnShortenedUrl() {
        String originalUrl = "https://example.com";
        String hash = "abc123";
        String baseUrl = "http://short.url";
        String expectedShortenedUrl = "http://short.url/abc123";

        ShortenUrlRequest request = ShortenUrlRequest.builder()
                .url(originalUrl)
                .build();
        Url url = new Url();
        url.setUrl(originalUrl);

        when(hashCache.getHash()).thenReturn(hash);
        when(domainConfig.getBaseUrl()).thenReturn(baseUrl);
        when(urlMapper.toUrl(request)).thenReturn(url);
        when(urlRepository.save(any(Url.class))).thenReturn(url);

        String result = urlService.shortenUrl(request);

        assertEquals(expectedShortenedUrl, result);
        verify(hashCache).getHash();
        verify(urlRepository).save(url);
        verify(urlCacheRepository).cacheUrl(hash, originalUrl);

        ArgumentCaptor<Url> urlCaptor = ArgumentCaptor.forClass(Url.class);
        verify(urlRepository).save(urlCaptor.capture());
        assertEquals(hash, urlCaptor.getValue().getHash());
    }

    @Test
    void getOriginalUrl_WhenUrlExists_ShouldReturnOriginalUrl() {
        String hash = "abc123";
        String originalUrl = "https://example.com";
        Url url = new Url();
        url.setUrl(originalUrl);

        when(urlRepository.findByHash(hash)).thenReturn(Optional.of(url));

        String result = urlService.getOriginalUrl(hash);

        assertEquals(originalUrl, result);
        verify(urlRepository).findByHash(hash);
    }

    @Test
    void getOriginalUrl_WhenUrlDoesNotExist_ShouldThrowException() {
        String hash = "abc123";
        when(urlRepository.findByHash(hash)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> urlService.getOriginalUrl(hash));
        verify(urlRepository).findByHash(hash);
    }

    @Test
    void deleteExpiredUrls_ShouldReturnDeletedUrls() {
        int batchSize = 10;
        List<Url> expiredUrls = Arrays.asList(
                new Url(),
                new Url()
        );
        when(urlRepository.deleteExpiredUrls(batchSize)).thenReturn(expiredUrls);

        List<Url> result = urlService.deleteExpiredUrls(batchSize);

        assertEquals(expiredUrls, result);
        verify(urlRepository).deleteExpiredUrls(batchSize);
    }

    @Test
    void shortenUrl_WhenHashIsNull_ShouldThrowException() {
        ShortenUrlRequest request = ShortenUrlRequest.builder()
                .url("https://example.com")
                .build();
        when(hashCache.getHash()).thenReturn(null);

        assertThrows(NullPointerException.class, () -> urlService.shortenUrl(request));
        verify(urlRepository, never()).save(any());
        verify(urlCacheRepository, never()).cacheUrl(any(), any());
    }

}
