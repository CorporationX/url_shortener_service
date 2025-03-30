package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.HashCache;
import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.util.UrlBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {
    @Mock
    private HashCache hashCache;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    HashRepository hashRepository;

    @Mock
    private UrlBuilder urlBuilder;

    @InjectMocks
    private UrlService urlService;

    private String originalUrl;
    private String hash;

    @BeforeEach
    void setUp() {
        originalUrl = "http://example.com/long-url";
        hash = "abc123";
        ReflectionTestUtils.setField(urlService, "expiredPeriod", 1L);
    }

    @Test
    void testGetShortUrl_returnsShortUrl() throws Exception {
        URL expectedShortUrl = new URL("http://short.url/abc123");

        when(hashCache.getHash()).thenReturn(hash);
        when(urlBuilder.createShortUrl(hash)).thenReturn(expectedShortUrl);

        URL result = urlService.getShortUrl(originalUrl);

        assertNotNull(result, "Short URL should not be null");
        assertEquals(expectedShortUrl, result);

        verify(urlRepository).save(any(Url.class));
        verify(urlCacheRepository).save(eq(hash), eq(originalUrl));
    }

    @Test
    void testGetOriginalUrl_returnsUrlFromCache() {
        String cachedUrl = "http://example.com/cached";

        when(urlCacheRepository.getUrl(hash)).thenReturn(cachedUrl);

        String result = urlService.getOriginalUrl(hash);

        assertEquals(cachedUrl, result);
        verify(urlCacheRepository).getUrl(hash);
        verifyNoInteractions(urlRepository);
    }

    @Test
    void testGetOriginalUrl_returnsUrlFromRepository() {
        when(urlCacheRepository.getUrl(hash)).thenReturn(null);

        when(urlRepository.findByHash(hash)).thenReturn(Optional.of(originalUrl));

        String result = urlService.getOriginalUrl(hash);

        assertEquals(originalUrl, result);
        verify(urlCacheRepository).getUrl(hash);
        verify(urlRepository).findByHash(hash);
    }

    @Test
    void testGetOriginalUrl_throwsExceptionWhenNotFound() {
        when(urlCacheRepository.getUrl(hash)).thenReturn(null);
        when(urlRepository.findByHash(hash)).thenReturn(Optional.empty());
        when(hashRepository.save(new Hash(hash))).thenReturn(any());

        assertThrows(NoSuchElementException.class, () -> urlService.getOriginalUrl(hash));
        verify(urlCacheRepository).getUrl(hash);
        verify(urlRepository).findByHash(hash);
    }

    @Test
    void testRemoveExpiredUrls() {
        List<String> expiredHashes = Arrays.asList("hash11", "hash21", "hash31");
        when(urlRepository.deleteExpiredUrlsAndReturnHashes()).thenReturn(expiredHashes);

        urlService.removeExpiredUrls();

        verify(urlRepository, times(1)).deleteExpiredUrlsAndReturnHashes();
        verify(hashRepository, times(1)).saveHashes(expiredHashes.toArray(new String[0]));
    }
}
