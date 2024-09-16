package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.HashCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    private static final String HOST_NAME = "http://sh.c";

    @Mock
    private HashCache hashCache;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private UrlCacheRepository urlCacheRepository;
    private UrlService urlService;

    @BeforeEach
    void setup() {
        urlService = new UrlService(hashCache, urlRepository, urlCacheRepository, HOST_NAME);
    }

    @Test
    void testShortenUrl() {
        String longUrl = "https://www.example.com";
        String hash = "abc123";
        String expectedShortUrl = HOST_NAME + "/" + hash;

        when(hashCache.getHash()).thenReturn(hash);

        String result = urlService.shortenUrl(longUrl);

        assertEquals(expectedShortUrl, result);
        verify(urlRepository).save(hash, longUrl);
        verify(urlCacheRepository).saveUrl(hash, longUrl);
    }

}