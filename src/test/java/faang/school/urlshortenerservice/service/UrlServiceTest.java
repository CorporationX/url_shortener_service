package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

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

    private final String shortUrlSuffix = "http://short.url";
    private final long ttl = 3600L;

    @BeforeEach
    void setUp() {
        setField(urlService, "shortUrlSuffix", shortUrlSuffix);
        setField(urlService, "shortUrlTtlInSeconds", ttl);
    }

    @Test
    void givenHashInCache_whenGetUrl_thenReturnFromCache() {
        String hash = "abc123";
        String expectedUrl = "https://example.com";
        when(urlCacheRepository.getUrl(hash)).thenReturn(expectedUrl);

        String actualUrl = urlService.getUrl(hash);

        assertEquals(expectedUrl, actualUrl);
        verify(urlCacheRepository).getUrl(hash);
        verifyNoInteractions(urlRepository);
    }

    @Test
    void givenHashNotInCacheAndInDb_whenGetUrl_thenReturnFromDbAndSetCache() {
        String hash = "abc123";
        String expectedUrl = "https://example.com";
        Url url = Url.builder().url(expectedUrl).hash(hash).build();

        when(urlCacheRepository.getUrl(hash)).thenReturn(null);
        when(urlRepository.findByHash(hash)).thenReturn(Optional.of(url));

        String actualUrl = urlService.getUrl(hash);

        assertEquals(expectedUrl, actualUrl);
        verify(urlRepository).findByHash(hash);
        verify(urlCacheRepository).setUrl(hash, expectedUrl);
    }

    @Test
    void givenHashNotFoundAnywhere_whenGetUrl_thenThrowException() {
        String hash = "missing";
        when(urlCacheRepository.getUrl(hash)).thenReturn(null);
        when(urlRepository.findByHash(hash)).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> urlService.getUrl(hash));
    }

    @Test
    void givenValidUrlDto_whenCreateShortUrl_thenReturnShortUrl() {
        UrlDto dto = new UrlDto("https://example.com");
        String hash = "abc123";

        when(hashCache.getHash()).thenReturn(hash);

        String shortUrl = urlService.createShortUrl(dto);

        verify(urlRepository).save(any(Url.class));
        verify(urlCacheRepository).setUrl(hash, dto.getUrl());
        assertEquals(shortUrlSuffix + "/" + hash, shortUrl);
    }
}
