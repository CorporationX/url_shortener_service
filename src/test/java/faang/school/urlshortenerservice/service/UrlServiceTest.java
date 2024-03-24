package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

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

    private String origianUrl;
    private String shortUrl;
    private String hash;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "urlPrefix", "http://short");
        origianUrl = "https://www.testLooooooooooooooongUrl.com";
        shortUrl = "http://short.com";
        hash = "hash";
    }

    @Test
    void getShortUrl() {
        Mockito.when(hashCache.getHash()).thenReturn(hash);

        String shortUrl = urlService.getShortUrl(origianUrl);

        assertEquals("http://short" + hash, shortUrl);
        Mockito.verify(urlRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(urlCacheRepository, Mockito.times(1)).save(hash, origianUrl);
    }

    @Test
    void getOriginalUrlCacheTest() {
        Mockito.when(urlCacheRepository.get(hash)).thenReturn(origianUrl);

        Assert.assertEquals(origianUrl, urlService.getOriginalUrl(hash));
    }

    @Test
    void getOriginalUrlRepositoryTest() {
        Mockito.when(urlRepository.findByHash(hash)).thenReturn(new Url(hash, origianUrl, LocalDateTime.now()));

        Assert.assertEquals(origianUrl, urlService.getOriginalUrl(hash));
    }
}