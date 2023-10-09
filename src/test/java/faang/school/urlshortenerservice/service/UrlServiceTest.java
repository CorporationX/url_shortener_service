package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    UrlRepository urlRepository;
    @Mock
    UrlCacheRepository urlCacheRepository;
    @Mock
    HashCache hashCache;

    @InjectMocks
    UrlService urlService;

    @Test
    void testGetOriginalUrlWithCache() {
        when(urlCacheRepository.getByHash("1"))
                .thenReturn(Optional.of("http://google.com"));

        String originalUrl = urlService.getOriginalUrl("1");

        assertEquals("http://google.com", originalUrl);
    }

    @Test
    void testGetOriginalUrlWithoutCache() {
        when(urlCacheRepository.getByHash("1"))
                .thenReturn(Optional.empty());
        when(urlRepository.getByHash("1"))
                .thenReturn(Optional.of("http://google.com"));

        String originalUrl = urlService.getOriginalUrl("1");

        assertEquals("http://google.com", originalUrl);
    }

    @Test
    void testGetOriginalUrlWithException() {
        when(urlCacheRepository.getByHash("1"))
                .thenReturn(Optional.empty());
        when(urlRepository.getByHash("1"))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> urlService.getOriginalUrl("1"));

        assertEquals("Url not found", exception.getMessage());
    }

    @Test
    void testCreateShortenedUrl() {
        when(hashCache.getHash())
                .thenReturn("8tQciGpd");

        String shortenedUrl = urlService.createShortenedUrl(
                        UrlDto.builder()
                                .url("http://google.com")
                                .build()
                );

        verify(urlRepository).save(Url.builder()
                .hash("8tQciGpd")
                .url("http://google.com")
                .build());

        assertEquals("http://null/8tQciGpd", shortenedUrl);
    }
}