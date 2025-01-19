package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.managers.HashCache;
import faang.school.urlshortenerservice.redis.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlCacheRepository urlCacheRepository;

    @Mock
    private HashCache hashCache;

    @InjectMocks
    private UrlService urlService;

    private String longUrl;
    private String shortUrl;
    private String hash;

    @BeforeEach
    public void setUp() {
        longUrl = "http://example.com/very/long/url";
        hash = "abc123";
        shortUrl = "http://short.url/" + hash;
    }

    @Test
    public void testCreateShortUrl() {
        when(hashCache.getHash()).thenReturn(hash);

        String result = urlService.createShortUrl(longUrl);

        assertEquals(shortUrl, result);
        verify(hashCache, times(1)).getHash();
        verify(urlRepository, times(1)).save(any(Url.class));
        verify(urlCacheRepository, times(1)).save(anyString(), anyString());
    }
}
