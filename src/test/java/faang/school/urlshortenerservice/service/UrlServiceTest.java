package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.LongUrlDto;
import faang.school.urlshortenerservice.entity.ShortUrl;
import faang.school.urlshortenerservice.exeption.InvalidURLException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {
    @Mock
    UrlRepository urlRepository;

    @Mock
    UrlCacheRepository urlCacheRepository;

    @Mock
    HashCache hashCache;

    @InjectMocks
    private UrlService urlService;

    private String hostName = "https://localhost:8099/";
    private LongUrlDto longUrlDto;
    private String hash;

    @BeforeEach
    void setUp() {
        hash = "123abc";
        urlService.setHostName(hostName);
        longUrlDto = new LongUrlDto("http:/google.com");
    }

    @Test
    @DisplayName("Test URL validation")
    void test_createShortUrl_urlValidation() {

        when(hashCache.getHashFromCache()).thenReturn(hash);

        urlService.createShortUrl(longUrlDto);

        assertDoesNotThrow(() -> urlService.createShortUrl(new LongUrlDto("  http:/google.com")));
        assertDoesNotThrow(() -> urlService.createShortUrl(new LongUrlDto("  http:/google.com  ")));
        assertDoesNotThrow(() -> urlService.createShortUrl(new LongUrlDto("  https://google.com//news  ")));
        assertThrows(InvalidURLException.class, () -> urlService.createShortUrl(new LongUrlDto("justAString")));
        assertThrows(InvalidURLException.class, () -> urlService.createShortUrl(new LongUrlDto("tel: +100")));
        assertThrows(InvalidURLException.class, () -> urlService.createShortUrl(new LongUrlDto("google.com")));
        assertThrows(InvalidURLException.class, () -> urlService.createShortUrl(new LongUrlDto("http:/   google.com")));
    }

    @Test
    @DisplayName("Test short URL created success")
    void test_createShortUrl_success() {
        when(hashCache.getHashFromCache()).thenReturn(hash);

        String result = urlService.createShortUrl(longUrlDto);

        verify(urlRepository, times(1)).save(any(ShortUrl.class));
        verify(urlCacheRepository, times(1)).saveToCache(hash, longUrlDto.url());

        assertNotNull(result);
        assertEquals("https://localhost:8099/123abc", result);
    }

    @Test
    @DisplayName("Test get real URL from cache success")
    void test_getUrl_WhenValidInput_ReturnsFromCache() {
        String hash = "abc";
        String longUrl = "https://google.com";

        when(urlCacheRepository.getFromCache(anyString())).thenReturn(Optional.of(longUrl));

        String result = urlService.getUrl(hash);

        verify(urlCacheRepository, times(1)).getFromCache(anyString());
        verify(urlRepository, never()).findById(anyString());
        verify(urlCacheRepository, never()).saveToCache(anyString(), anyString());

        assertNotNull(result);
        assertEquals(longUrl, result);
    }

    @Test
    @DisplayName("Test get real URL from database")
    void test_getUrl_WhenValidInput_ReturnsFromDB() {
        String hash = "abc";
        String longUrl = "https://google.com";
        ShortUrl urlEntity = ShortUrl.builder()
                .url(longUrl)
                .build();

        when(urlCacheRepository.getFromCache(anyString())).thenReturn(Optional.empty());
        when(urlRepository.findById(anyString())).thenReturn(Optional.of(urlEntity));

        String result = urlService.getUrl(hash);

        verify(urlCacheRepository, times(1)).getFromCache(anyString());
        verify(urlRepository, times(1)).findById(anyString());
        verify(urlCacheRepository, times(1)).saveToCache(anyString(), anyString());

        assertNotNull(result);
        assertEquals(longUrl, result);
    }

    @Test
    @DisplayName("Test get real URL from database fail - no such url in DB")
    void test_getUrl_WhenInvalidInput_ReturnsException() {
        String hash = "abc";

        when(urlCacheRepository.getFromCache(anyString())).thenReturn(Optional.empty());
        when(urlRepository.findById(anyString())).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> urlService.getUrl(hash));
        assertEquals("URL matching provided hash 'abc' not found", ex.getMessage());

        verify(urlCacheRepository, times(1)).getFromCache(anyString());
        verify(urlRepository, times(1)).findById(anyString());
        verify(urlCacheRepository, never()).saveToCache(anyString(), anyString());
    }
}
