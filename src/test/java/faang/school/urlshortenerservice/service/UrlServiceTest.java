package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.UrlHash;
import faang.school.urlshortenerservice.model.UrlHashRedis;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    private static final String URL = "http://www.google.com/very/long/url";
    private static final String HASH = "Hs5FiD0";

    @Mock
    private HashCache hashCache;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlCacheRepository urlCacheRepository;

    @InjectMocks
    private UrlService urlService;

    UrlHash urlHash = UrlHash.builder()
            .hash(HASH)
            .url(URL)
            .build();
    UrlHashRedis urlHashRedis = UrlHashRedis.builder()
            .id(HASH)
            .url(URL)
            .build();

    @Test
    public void createShortLinkTest() {
        when(hashCache.getHash()).thenReturn(HASH);

        String actualResult = urlService.createShortLink(URL);

        verify(urlRepository).save(urlHash);
        verify(urlCacheRepository).save(urlHashRedis);
        assertEquals(HASH, actualResult);
    }

    @Test
    public void createShortLinkTestWhenUrlInDb() {
        when(urlRepository.findByUrl(URL)).thenReturn(Optional.of(urlHash));

        String actualResult = urlService.createShortLink(URL);

        verify(urlRepository, never()).save(any(UrlHash.class));
        verify(urlCacheRepository, never()).save(any(UrlHashRedis.class));
        assertEquals(HASH, actualResult);
    }

    @Test
    public void getOriginalUrlWhenInRedis() {
        when(urlCacheRepository.findById(HASH)).thenReturn(Optional.of(urlHashRedis));

        String actualResult = urlService.getOriginalUrlByHash(HASH);

        assertEquals(URL, actualResult);
        verify(urlRepository, never()).findById(HASH);
    }

    @Test
    public void getOriginalUrlWhenInDb() {
        when(urlRepository.findById(HASH)).thenReturn(Optional.of(urlHash));

        String actualResult = urlService.getOriginalUrlByHash(HASH);

        assertEquals(URL, actualResult);
        verify(urlCacheRepository).findById(HASH);
        verify(urlRepository).findById(HASH);
    }

    @Test
    public void getOriginalUrlWhenNotFound() {
        String errMessage = String.format("Original URL not found by hash: %s in DB", HASH);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> urlService.getOriginalUrlByHash(HASH));

        assertEquals(errMessage, exception.getMessage());
    }

}