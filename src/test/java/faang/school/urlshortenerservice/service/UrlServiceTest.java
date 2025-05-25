package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.NoAvailableHashException;
import faang.school.urlshortenerservice.exception.SQLSaveException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private HashCache hashCache;

    @InjectMocks
    private UrlService urlService;

    @BeforeEach
    void setUp() {
        // Устанавливаем base_url через рефлексию
        ReflectionTestUtils.setField(urlService, "base_url", "http://short.url/");
    }

    @Test
    void givenValidUrlAndHash_whenSaveOriginal_thenSavesAndReturnsShortUrl() {
        String url = "https://example.com";
        String hash = "abc123";
        when(hashCache.getHash()).thenReturn(Optional.of(hash));
        doNothing().when(urlRepository).save(hash, url);
        doNothing().when(urlCacheRepository).save(hash, url);

        String result = urlService.saveOriginalUrl(url);

        verify(hashCache, times(1)).getHash();
        verify(urlRepository, times(1)).save(hash, url);
        verify(urlCacheRepository, times(1)).save(hash, url);
        assertEquals("http://short.url/abc123", result);
    }

    @Test
    void givenNoAvailableHash_whenSaveOriginalUrl_thenThrowsNoAvailableHashException() {
        String url = "https://example.com";
        when(hashCache.getHash()).thenReturn(Optional.empty());

        assertThrows(NoAvailableHashException.class, () -> urlService.saveOriginalUrl(url));
        verify(hashCache, times(1)).getHash();
        verifyNoInteractions(urlRepository, urlCacheRepository);
    }

    @Test
    void givenRepositoryThrowsException_whenSaveOriginalUrl_thenThrowsSQLSaveException() {
        String url = "https://example.com";
        String hash = "abc123";
        DataAccessException exception = new DataAccessException("DB error") {
        };
        when(hashCache.getHash()).thenReturn(Optional.of(hash));
        doThrow(exception).when(urlRepository).save(hash, url);

        assertThrows(SQLSaveException.class, () -> urlService.saveOriginalUrl(url));
        verify(hashCache, times(1)).getHash();
        verify(urlRepository, times(1)).save(hash, url);
        verifyNoInteractions(urlCacheRepository);
    }

    @Test
    void givenRedisThrowsException_whenSaveOriginalUrlCalled_thenSavesToPostgresAndReturnsShortUrl() {
        String url = "https://example.com";
        String hash = "abc123";
        RuntimeException redisException = new RuntimeException("Redis error");
        when(hashCache.getHash()).thenReturn(Optional.of(hash));
        doNothing().when(urlRepository).save(hash, url);
        doThrow(redisException).when(urlCacheRepository).save(hash, url);

        String result = urlService.saveOriginalUrl(url);

        verify(hashCache, times(1)).getHash();
        verify(urlRepository, times(1)).save(hash, url);
        verify(urlCacheRepository, times(1)).save(hash, url);
        assertEquals("http://short.url/abc123", result);
    }

    @Test
    void givenUrlInCache_whenGetOriginalUrl_thenReturnsUrlFromCache() {
        String hash = "abc123";
        String url = "https://example.com";
        when(urlCacheRepository.findUrlByHash(hash)).thenReturn(Optional.of(url));

        String result = urlService.getOriginalUrl(hash);

        verify(urlCacheRepository, times(1)).findUrlByHash(hash);
        verifyNoInteractions(urlRepository, hashCache);
        assertEquals(url, result);
    }

    @Test
    void givenUrlNotInCacheButInPostgres_whenGetOriginalUrl_thenReturnsUrlAndCachesIt() {
        String hash = "abc123";
        String url = "https://example.com";
        when(urlCacheRepository.findUrlByHash(hash)).thenReturn(Optional.empty());
        when(urlRepository.findByHash(hash)).thenReturn(Optional.of(url));
        doNothing().when(urlCacheRepository).save(hash, url);

        String result = urlService.getOriginalUrl(hash);

        verify(urlCacheRepository, times(1)).findUrlByHash(hash);
        verify(urlRepository, times(1)).findByHash(hash);
        verify(urlCacheRepository, times(1)).save(hash, url);
        assertEquals(url, result);
    }

    @Test
    void givenUrlNotFound_whenGetOriginalUrl_thenThrowsUrlNotFoundException() {
        String hash = "abc123";
        when(urlCacheRepository.findUrlByHash(hash)).thenReturn(Optional.empty());
        when(urlRepository.findByHash(hash)).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> urlService.getOriginalUrl(hash));
        verify(urlCacheRepository, times(1)).findUrlByHash(hash);
        verify(urlRepository, times(1)).findByHash(hash);
        verifyNoMoreInteractions(urlCacheRepository);
    }

    @Test
    void givenRedisThrowsExceptionOnCacheSave_whenGetOriginalUrl_thenReturnsUrlFromPostgres() {
        String hash = "abc123";
        String url = "https://example.com";
        RuntimeException redisException = new RuntimeException("Redis error");
        when(urlCacheRepository.findUrlByHash(hash)).thenReturn(Optional.empty());
        when(urlRepository.findByHash(hash)).thenReturn(Optional.of(url));
        doThrow(redisException).when(urlCacheRepository).save(hash, url);

        String result = urlService.getOriginalUrl(hash);

        verify(urlCacheRepository, times(1)).findUrlByHash(hash);
        verify(urlRepository, times(1)).findByHash(hash);
        verify(urlCacheRepository, times(1)).save(hash, url);
        assertEquals(url, result);
    }

    @Test
    void givenValidHashAndUrl_whenSaveToRedis_thenSavesToRedis() {
        String hash = "abc123";
        String url = "https://example.com";
        doNothing().when(urlCacheRepository).save(hash, url);

        ReflectionTestUtils.invokeMethod(urlService, "saveToRedis", hash, url);

        verify(urlCacheRepository, times(1)).save(hash, url);
    }

    @Test
    void givenRedisThrowsException_whenSaveToRedis_thenLogsWarningAndContinues() {
        String hash = "abc123";
        String url = "https://example.com";
        RuntimeException redisException = new RuntimeException("Redis error");
        doThrow(redisException).when(urlCacheRepository).save(hash, url);

        ReflectionTestUtils.invokeMethod(urlService, "saveToRedis", hash, url);

        verify(urlCacheRepository, times(1)).save(hash, url);
    }
}