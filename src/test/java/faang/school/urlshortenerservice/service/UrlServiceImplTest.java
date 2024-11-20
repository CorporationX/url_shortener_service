package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.redis.RedisCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.url.UrlServiceImpl;
import faang.school.urlshortenerservice.cache.hash.HashCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceImplTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private RedisCache redisCache;

    @Mock
    private HashCache hashCache;

    @InjectMocks
    private UrlServiceImpl urlService;

    private String url;
    private String hash;

    @BeforeEach
    void setUp() {
        url = "https://shortener.com";
        hash = "hash";
    }

    @Test
    void getLongUrlByHash_When_HashFoundInCache() {
        when(redisCache.getFromCache(hash)).thenReturn(Optional.of(url));

        String result = urlService.getLongUrlByHash(hash);

        assertEquals(url, result);
        verify(redisCache, times(1)).getFromCache(hash);
        verify(urlRepository, times(0)).findUrlByHash(hash);
    }

    @Test
    void getLongUrlByHash_When_HashNotFoundInCache() {
        when(redisCache.getFromCache(hash)).thenReturn(Optional.empty());
        when(urlRepository.findUrlByHash(hash)).thenReturn(Optional.of(url));

        String result = urlService.getLongUrlByHash(hash);

        assertEquals(url, result);
        verify(redisCache, times(1)).getFromCache(hash);
        verify(urlRepository, times(1)).findUrlByHash(hash);
    }

    @Test
    void getLongUrlByHash_When_HashNotFound() {
        when(redisCache.getFromCache(hash)).thenReturn(Optional.empty());
        when(urlRepository.findUrlByHash(hash)).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> urlService.getLongUrlByHash(hash));
    }

    @Test
    void generateHashForUrl_When_HashIsGenerated() {
        when(hashCache.getHash()).thenReturn(hash);

        String result = urlService.generateHashForUrl(url);

        assertEquals(url, result);
        verify(hashCache, times(1)).getHash();
        verify(redisCache, times(1)).saveToCache(hash, url);
        verify(urlRepository, times(1)).save(any(Url.class));
    }
}
