package faang.school.urlshortenerservice.service.impl;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.model.dto.url.UrlDto;
import faang.school.urlshortenerservice.model.entity.Hash;
import faang.school.urlshortenerservice.model.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {
    @InjectMocks
    private UrlServiceImpl urlService;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private HashCache hashCache;
    @Mock
    private UrlCacheRepository urlCacheRepository;

    private UrlDto urlOriginalUrlDto;
    private UrlDto urlShortUrlDto;
    private Url url;
    private String originalUrl;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "format", "http://short.url/%s");
        originalUrl = "http://short.url/test.com";
        urlOriginalUrlDto = UrlDto.builder()
                .url("https://test.com")
                .build();
        urlShortUrlDto = UrlDto.builder()
                .url("test")
                .build();
        url = new Url();
        url.setHash("test");
        url.setUrl("http://short.url/test.com");
    }

    @Test
    void createShortUrlSuccess() {
        String shortUrl = "http://short.url/test";
        Hash hash = new Hash("test");

        Mockito.when(hashCache.getHash()).thenReturn(hash);
        Mockito.when(urlRepository.saveAndFlush(Mockito.any(Url.class))).thenReturn(url);

        var result = urlService.createShortUrl(urlOriginalUrlDto);

        assertNotNull(result);
        assertEquals(shortUrl, result);

        Mockito.verify(hashCache, Mockito.times(1)).getHash();
        Mockito.verify(urlRepository, Mockito.times(1)).saveAndFlush(Mockito.any(Url.class));
        Mockito.verify(urlCacheRepository, Mockito.times(1)).saveHash(Mockito.any(Url.class));
    }

    @Test
    void getOriginalUrlSuccessReturnUrlCache() {
        Mockito.when(urlCacheRepository.getUrl(Mockito.anyString())).thenReturn(originalUrl);
        var result = urlService.getOriginalUrl(urlShortUrlDto);

        assertNotNull(result);
        assertEquals(originalUrl, result);

        Mockito.verify(urlCacheRepository, Mockito.times(1)).getUrl(Mockito.anyString());
    }

    @Test
    void getOriginalUrlSuccessReturnUrlDataBase() {
        Mockito.when(urlCacheRepository.getUrl(Mockito.anyString())).thenReturn(null);
        Mockito.when(urlRepository.findByHash(Mockito.anyString())).thenReturn(Optional.of(url));

        var result = urlService.getOriginalUrl(urlShortUrlDto);

        assertNotNull(result);
        assertEquals(originalUrl, result);

        Mockito.verify(urlCacheRepository, Mockito.times(1)).getUrl(Mockito.anyString());
        Mockito.verify(urlRepository, Mockito.times(1)).findByHash(Mockito.anyString());
        Mockito.verify(urlCacheRepository, Mockito.times(1)).saveHash(Mockito.any(Url.class));
    }

    @Test
    void getOriginalUrlReturnException() {
        Mockito.when(urlCacheRepository.getUrl(Mockito.anyString())).thenReturn(null);
        Mockito.when(urlRepository.findByHash(Mockito.anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> urlService.getOriginalUrl(urlShortUrlDto));
    }
}