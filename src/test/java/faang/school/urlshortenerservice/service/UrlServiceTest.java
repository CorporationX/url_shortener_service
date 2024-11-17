package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotfoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.cache.HashCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private HashRepository hashRepository;

    @InjectMocks
    private UrlService urlService;

    private final String staticAddress = "http://static-address/";
    private final int urlTtlAtCache = 7;
    private final String strUrl = "http://example.com";
    private final String hash = "abc123";
    private Url url;

    @BeforeEach
    void setUp() throws Exception {
        Field staticAddressField = UrlService.class.getDeclaredField("staticAddress");
        staticAddressField.setAccessible(true);
        staticAddressField.set(urlService, staticAddress);

        Field urlTtlAtCacheField = UrlService.class.getDeclaredField("urlTtlAtCache");
        urlTtlAtCacheField.setAccessible(true);
        urlTtlAtCacheField.set(urlService, urlTtlAtCache);

        Field urlTtlAtRepoField = UrlService.class.getDeclaredField("urlTtlAtRepo");
        urlTtlAtRepoField.setAccessible(true);
        int urlTtlAtRepo = 1;
        urlTtlAtRepoField.set(urlService, urlTtlAtRepo);

        url = Url.builder()
                .url(strUrl)
                .hash(hash)
                .build();
    }

    @Test
    void testGetUrlFromCache() {
        when(urlCacheRepository.findByHash(hash)).thenReturn(url);

        String result = urlService.getUrl(hash);

        assertEquals(strUrl, result);
        verify(urlCacheRepository, times(1)).findByHash(hash);
        verify(urlRepository, times(0)).findById(hash);
    }

    @Test
    void testGetUrlFromRepository() {
        when(urlCacheRepository.findByHash(hash)).thenReturn(null);
        when(urlRepository.findById(hash)).thenReturn(Optional.of(url));

        String result = urlService.getUrl(hash);

        assertEquals(strUrl, result);
        verify(urlCacheRepository).findByHash(hash);
        verify(urlRepository).findById(hash);
        verify(urlCacheRepository).saveUrl(url, urlTtlAtCache);
    }

    @Test
    void testUrlNotFound() {
        when(urlCacheRepository.findByHash(hash)).thenReturn(null);
        when(urlRepository.findById(hash)).thenReturn(Optional.empty());

        UrlNotfoundException exception = assertThrows(UrlNotfoundException.class, () -> urlService.getUrl(hash));
        assertEquals(String.format("Url by hash: %s not found", hash), exception.getMessage());
    }

    @Test
    void testCreateHash() {
        when(hashCache.getHash()).thenReturn(hash);

        String result = urlService.createHash(strUrl);

        assertEquals(staticAddress + hash, result);
        verify(urlRepository).save(any(Url.class));
        verify(urlCacheRepository).saveUrl(any(Url.class), eq(urlTtlAtCache));
    }

    @Test
    void testCleanOldUrlsAndSavingFreedHashes() {
        List<Hash> freedHashes = List.of(new Hash(hash));
        when(urlRepository.deleteOldUrlsAndGetFreedHashes(any())).thenReturn(freedHashes);

        urlService.cleanOldUrlsAndSavingFreedHashes();

        verify(urlRepository).deleteOldUrlsAndGetFreedHashes(any());
        verify(hashRepository).saveAll(freedHashes);
    }
}