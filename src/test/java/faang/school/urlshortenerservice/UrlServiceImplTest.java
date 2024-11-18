package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.local.cache.LocalCache;
import faang.school.urlshortenerservice.model.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.impl.UrlServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UrlServiceImplTest {

    @InjectMocks
    private UrlServiceImpl urlService;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private LocalCache localCache;

    @Value("${hash.cache.ttl-in-hours:24}")
    private int ttlInHours = 24;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        urlService = new UrlServiceImpl(urlCacheRepository, urlRepository, hashRepository, localCache);
        ReflectionTestUtils.setField(urlService, "ttlInHours", ttlInHours);
    }

    @Test
    void testGetUrlByHash_FromCache() {
        String hash = "abc123";
        String cachedUrl = "https://www.cached.com";

        when(urlCacheRepository.findUrlByHash(hash)).thenReturn(cachedUrl);

        String result = urlService.getUrlByHash(hash);

        assertEquals(cachedUrl, result);
        verify(urlCacheRepository, times(1)).findUrlByHash(hash);
        verify(urlCacheRepository, times(1)).saveUrlWithExpiry(hash, cachedUrl, ttlInHours);
        verify(urlRepository, never()).findById(anyString());
    }

    @Test
    void testGetUrlByHash_FromRepository() {
        String hash = "def456";
        String originalUrl = "https://www.original.com";

        when(urlCacheRepository.findUrlByHash(hash)).thenReturn(null);
        when(urlRepository.findById(hash)).thenReturn(Optional.of(new Url(hash, originalUrl)));

        String result = urlService.getUrlByHash(hash);

        assertEquals(originalUrl, result);
        verify(urlCacheRepository, times(1)).findUrlByHash(hash);
        verify(urlRepository, times(1)).findById(hash);
        verify(urlCacheRepository, times(1)).saveUrlWithExpiry(hash, originalUrl, ttlInHours);
    }

    @Test
    void testGetUrlByHash_NotFound() {
        String hash = "ghi789";

        when(urlCacheRepository.findUrlByHash(hash)).thenReturn(null);
        when(urlRepository.findById(hash)).thenReturn(Optional.empty());

        UrlNotFoundException exception = assertThrows(UrlNotFoundException.class, () -> {
            urlService.getUrlByHash(hash);
        });

        assertEquals("URL not found for hash: " + hash, exception.getMessage());
        verify(urlCacheRepository, times(1)).findUrlByHash(hash);
        verify(urlRepository, times(1)).findById(hash);
        verify(urlCacheRepository, never()).saveUrlWithExpiry(anyString(), anyString(), anyInt());
    }

    @Test
    void testCreateShortUrl_Success() {
        String originalUrl = "https://www.example.com";
        String hash = "xyz123";

        when(localCache.getHash()).thenReturn(hash);

        String result = urlService.createShortUrl(originalUrl);

        assertEquals(hash, result);
        verify(localCache, times(1)).getHash();
        verify(urlRepository, times(1)).save(any(Url.class));
        verify(urlCacheRepository, times(1)).saveUrlWithExpiry(hash, originalUrl, ttlInHours);
    }

    @Test
    void testCleanUrl_WithFreedHashes() {
        List<String> freedHashes = Arrays.asList("hash1", "hash2");

        when(urlRepository.deleteOldUrlsAndReturnHashes()).thenReturn(freedHashes);

        urlService.cleanUrl();

        verify(urlRepository, times(1)).deleteOldUrlsAndReturnHashes();
        verify(hashRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testCleanUrl_NoFreedHashes() {
        List<String> freedHashes = List.of();

        when(urlRepository.deleteOldUrlsAndReturnHashes()).thenReturn(freedHashes);

        urlService.cleanUrl();

        verify(urlRepository, times(1)).deleteOldUrlsAndReturnHashes();
        verify(hashRepository, never()).saveAll(anyList());
    }
}
