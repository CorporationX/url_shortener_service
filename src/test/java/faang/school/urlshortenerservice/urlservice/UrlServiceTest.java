package faang.school.urlshortenerservice.urlservice;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.ShortenResponse;
import faang.school.urlshortenerservice.exceptions.DataNotFoundException;
import faang.school.urlshortenerservice.repository.RedisCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.validator.UrlValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @InjectMocks
    private UrlService urlService;

    @Mock
    private UrlValidator urlValidator;

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private RedisCacheRepository redisCacheRepository;

    @Test
    void shorten_WithValidUrl_ShouldReturnShortenResponse() throws Exception {
        Field urlPathField = UrlService.class.getDeclaredField("urlPath");
        urlPathField.setAccessible(true);
        urlPathField.set(urlService, "http://localhost:8080/api/v1/url/");

        String url = "https://www.example.com";
        String hash = "abcdef";
        when(hashCache.getHash()).thenReturn(hash);

        ShortenResponse response = urlService.shorten(url);

        assertNotNull(response);
        assertEquals("http://localhost:8080/api/v1/url/abcdef", response.getShortUrl());
        verify(urlValidator, times(1)).validateUrl(url);
        verify(urlRepository, times(1)).save(hash, url);
        verify(redisCacheRepository, times(1)).savePair(hash, url);
    }

    @Test
    void resolve_WithValidHash_ShouldReturnUrl() {
        String hash = "abcdef";
        String url = "https://www.example.com";
        when(redisCacheRepository.getUrl(hash)).thenReturn(url);

        String resolvedUrl = urlService.resolve(hash);

        assertEquals(url, resolvedUrl);
        verify(redisCacheRepository, times(1)).getUrl(hash);
    }

    @Test
    void resolve_WithInvalidHash_ShouldThrowDataNotFoundException() {
        String hash = "invalidHash";
        when(redisCacheRepository.getUrl(hash)).thenReturn(null);
        when(urlRepository.findByHash(hash)).thenThrow(new DataNotFoundException());

        assertThrows(DataNotFoundException.class, () -> urlService.resolve(hash));
    }
}
