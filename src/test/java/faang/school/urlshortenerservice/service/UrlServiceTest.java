package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    @InjectMocks
    private UrlService urlService;

    @Mock
    private HashCache hashCache;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    private String hash;
    private String url;

    @BeforeEach
    void setUp() {
        hash = "Qw231";
        url = "https://anyurl/test";
    }

    @Test
    void createShortUrl() {
        when(hashCache.getHash()).thenReturn(hash);

        urlService.createShortUrl(url);
        verify(urlRepository).save(hash, url);
        verify(urlCacheRepository).putToCache(hash, url);
    }

    @Test
    void testGetOriginalUrlExistInCache() {
        when(urlCacheRepository.getFromCache(hash)).thenReturn(url);
        String actual = urlService.getOriginalUrl(hash);
        assertEquals(url, actual);
    }

    @Test
    void testGetOriginalUrlExistInDB() {
        when(urlCacheRepository.getFromCache(hash)).thenReturn(null);
        when(urlRepository.findByHash(hash)).thenReturn(Optional.of(url));
        String actual = urlService.getOriginalUrl(hash);
        assertEquals(url, actual);
    }

    @Test
    void testGetOriginalUrlNotExist() {
        when(urlCacheRepository.getFromCache(hash)).thenReturn(null);
        when(urlRepository.findByHash(hash)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                () -> urlService.getOriginalUrl(hash));
    }

    @Test
    void testRefreshFreeHash() {
        List<String> hashes = List.of(
                "qwer",
                "qwre",
                "rrfd"
        );

        when(urlRepository.deleteAndGetOldHash()).thenReturn(hashes);

        urlService.refreshFreeHash();
        verify(hashRepository).save(hashes);
    }
}
