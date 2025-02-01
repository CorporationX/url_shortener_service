package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.entity.Cache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.localcache.HashCache;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.validator.UrlValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.CacheManager;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UrlServiceTest {
    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private HashCache hash;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private UrlValidator urlValidator;

    @InjectMocks
    private UrlService urlService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getHash_shouldReturnUrlFromCache() {
        String hash = "abc123";
        String url = "https://example.com";
        Cache cachedValue = new Cache(hash, url, LocalDateTime.now());

        when(urlCacheRepository.findById(hash)).thenReturn(Optional.of(cachedValue));

        String result = urlService.getHash(hash);

        assertEquals(url, result);
        verify(urlCacheRepository).findById(hash);
        verifyNoInteractions(urlRepository);
    }

    @Test
    void getHash_shouldReturnUrlFromDatabaseWhenNotInCache() {
        String hash = "abc123";
        String url = "https://example.com";

        when(urlCacheRepository.findById(hash)).thenReturn(Optional.empty());
        when(urlRepository.findUrlByHash(hash)).thenReturn(url);

        String result = urlService.getHash(hash);

        assertEquals(url, result);
        verify(urlCacheRepository).findById(hash);
        verify(urlRepository).findUrlByHash(hash);
        verify(urlValidator).validateUrl(url, hash);
    }

    @Test
    void getHash_shouldValidateUrl() {
        String hash = "abc123";
        String url = "https://example.com";

        when(urlCacheRepository.findById(hash)).thenReturn(Optional.empty());
        when(urlRepository.findUrlByHash(hash)).thenReturn(url);

        String result = urlService.getHash(hash);

        assertEquals(url, result);
        verify(urlValidator).validateUrl(url, hash);
    }
}
