package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.local_cache.LocalCache;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.validator.UrlServiceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlServiceValidator urlServiceValidator;

    @Mock
    private LocalCache localCache;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @InjectMocks
    private UrlService urlService;

    private String link = "http://localhost:8080/api/v1/shorter/redirect/";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "link", link);
    }

    @Test
    public void testCreateNewShortUrl() throws Exception {
        URL originalUrl = new URL("http://original.url");
        String hash = "abc123";
        when(localCache.getCache()).thenReturn(hash);

        URL shortUrl = urlService.createNewShortUrl(originalUrl);

        assertEquals(new URL(link + hash), shortUrl);
        verify(urlRepository, times(1)).save(any(Url.class));
        verify(urlCacheRepository, times(1)).saveAtRedis(any(Url.class));
    }

    @Test
    public void testGetUrlFromRedis() throws MalformedURLException {
        String hash = "abc123";
        URL expectedUrl = new URL("http://original.url");
        when(urlCacheRepository.getFromRedis(hash)).thenReturn(expectedUrl);

        URL actualUrl = urlService.getUrl(hash);

        assertEquals(expectedUrl, actualUrl);
        verify(urlCacheRepository, times(1)).getFromRedis(hash);
        verify(urlRepository, times(0)).findByHash(hash); // убедитесь, что не вызывается findByHash
    }
    @Test
    public void testGetUrlFromDb_UrlNotFound() {
        String hash = "nonexistent_hash";
        when(urlCacheRepository.getFromRedis(hash)).thenReturn(null);
        when(urlRepository.findByHash(hash)).thenThrow(new RuntimeException("Not found"));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> urlService.getUrl(hash));
        assertEquals("can't redirect to main ulr , incorrect hash", thrown.getMessage());
        verify(urlRepository, times(1)).findByHash(hash);
    }
}