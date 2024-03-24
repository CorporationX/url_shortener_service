package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {
    @Mock
    private HashCache hashCache;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlCacheRepository urlCacheRepository;
    @InjectMocks
    private UrlService urlService;

    private String url;
    private String hash;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "urlPrefix", "http://short");
        url = "https://www.testLooooooooooooooongUrl.com";
        hash = "hash";
    }

    @Test
    void getShortUrl() {
        Mockito.when(hashCache.getHash()).thenReturn(hash);

        String shortUrl = urlService.getShortUrl(url);

        assertEquals("http://short" + hash, shortUrl);
        Mockito.verify(urlRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(urlCacheRepository, Mockito.times(1)).save(hash, url);
    }
}