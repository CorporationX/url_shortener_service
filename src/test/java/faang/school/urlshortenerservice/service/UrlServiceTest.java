package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.managers.HashCache;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashJdbcRepository;
import faang.school.urlshortenerservice.repository.redis.UrlCacheRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private HashCache hashCache;

    @Mock
    private HashJdbcRepository hashJdbcRepository;

    @InjectMocks
    private UrlService urlService;

    private String longUrl;
    private String shortUrl;
    private String hash;

    @BeforeEach
    public void setUp() {
        longUrl = "http://example.com/very/long/url";
        hash = "abc123";
        shortUrl = "http://faang.url/api/v1/url/" + hash;
    }

    @Test
    public void testCreateShortUrl_whenUrlIsNew() {
        when(hashCache.getHash()).thenReturn(hash);
        when(urlRepository.returnHashByUrlIfExists(longUrl)).thenReturn(null);

        String result = urlService.shortUrl(longUrl);

        assertEquals(shortUrl, result);
        verify(hashCache, times(1)).getHash();
        verify(urlRepository, times(1)).save(any(Url.class));
        verify(urlCacheRepository, times(1)).save(anyString(), anyString());
    }

    @Test
    public void testGetLongUrl_whenUrlFoundInCache() {
        when(urlCacheRepository.findUrlByHash(hash)).thenReturn(longUrl);

        String result = urlService.getLongUrl(hash);

        assertEquals(longUrl, result);
        verify(urlCacheRepository, times(1)).findUrlByHash(hash);
    }

    @Test
    public void testGetLongUrl_whenUrlNotInCacheButInDatabase() {
        when(urlCacheRepository.findUrlByHash(hash)).thenReturn(null);
        when(urlRepository.findUrlByHash(hash)).thenReturn(longUrl);

        String result = urlService.getLongUrl(hash);

        assertEquals(longUrl, result);
        verify(urlCacheRepository, times(1)).findUrlByHash(hash);
        verify(urlRepository, times(1)).findUrlByHash(hash);
        verify(urlCacheRepository, times(1)).save(hash, longUrl);
    }

    @Test
    public void testGetLongUrl_whenUrlNotFound() {
        when(urlCacheRepository.findUrlByHash(hash)).thenReturn(null);
        when(urlRepository.findUrlByHash(hash)).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> urlService.getLongUrl(hash));
    }

    @Test
    public void testCleanOldUrls() {
        when(urlRepository.deleteOldUrlsAndReturnHashes()).thenReturn(List.of(hash));

        urlService.cleanOldUrls();

        verify(hashJdbcRepository, times(1)).saveBatch(List.of(hash));
        verify(urlCacheRepository, times(1)).deleteOldHashes(List.of(hash));
    }
}
