package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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

    private String hash;
    private String originalUrl;

    @BeforeEach
    void setUp() {
        hash = "hash1";
        originalUrl = "http://example.com";
    }

    @Test
    void createShortUrl_shouldCreateAndSaveShortUrl() {
        when(hashCache.getHash()).thenReturn(hash);

        String shortUrl = urlService.createShortUrl(originalUrl);

        assertEquals(hash, shortUrl);
        verify(urlRepository).save(argThat(url -> url.getHash().equals(hash)));
        verify(urlCacheRepository).save(hash, originalUrl);
    }

    @Test
    void getOriginalUrl_shouldReturnCachedUrl() {
        when(urlCacheRepository.find(hash)).thenReturn(originalUrl);

        String result = urlService.getOriginalUrl(hash);

        assertEquals(originalUrl, result);
        verify(urlCacheRepository).find(hash);
        verifyNoInteractions(urlRepository);
    }

    @Test
    void getOriginalUrl_shouldFetchFromDbIfNotCached() {
        Url urlEntity = new Url(hash, originalUrl, LocalDateTime.now());

        when(urlCacheRepository.find(hash)).thenReturn(null);
        when(urlRepository.findByHash(hash)).thenReturn(urlEntity);

        String result = urlService.getOriginalUrl(hash);

        assertEquals(originalUrl, result);
        verify(urlCacheRepository).save(hash, originalUrl);
    }
}