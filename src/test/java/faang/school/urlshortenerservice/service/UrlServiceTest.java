package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFound;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

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


     @BeforeEach
     void setUp() {
         ReflectionTestUtils.setField(urlService, "host", "localhost");
         ReflectionTestUtils.setField(urlService, "protocol", "http");
     }
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
                .thenReturn(Optional.of(Url.builder().url("http://google.com").build()));

        String originalUrl = urlService.getOriginalUrl("1");

        assertEquals("http://google.com", originalUrl);
    }

    @Test
    void testGetOriginalUrlWithException() {
        when(urlCacheRepository.getByHash("1"))
                .thenReturn(Optional.empty());
        when(urlRepository.getByHash("1"))
                .thenReturn(Optional.empty());

        UrlNotFound exception = assertThrows(UrlNotFound.class,
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

        assertEquals("http://localhost/8tQciGpd", shortenedUrl);
    }
}