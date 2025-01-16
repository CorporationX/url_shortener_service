package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.LongUrlDto;
import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.entity.ShortUrl;
import faang.school.urlshortenerservice.exception.InvalidURLException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.HashCache;
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
class UrlServiceTest {

    @Mock
    UrlRepository urlRepository;

    @Mock
    UrlCacheService urlCacheService;

    @Mock
    HashCache hashCache;

    @InjectMocks
    private UrlService urlService;

    private String hostName = "http:/shrt.com/";
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

        ShortUrlDto result = urlService.createShortUrl(longUrlDto);

        verify(urlRepository, times(1)).save(any(ShortUrl.class));
        verify(urlCacheService, times(1)).saveToCache(hash, longUrlDto.url());

        assertNotNull(result);
        assertEquals("http:/shrt.com/123abc", result.shortUrl());
    }

    @Test
    @DisplayName("Test validation of short URL")
    void test_getUrl_validateShortUrl() {
        String shortUrl = "https://shrt.com/123abc";
        hash = "123abc";

        when(urlCacheService.getFromCache(anyString())).thenReturn(Optional.of(anyString()));

        urlService.getUrl(shortUrl);

        assertDoesNotThrow(() -> urlService.getUrl("https://shrt.com/a1b2c3"));
        assertThrows(InvalidURLException.class, () -> urlService.getUrl("notRealUrl"));
        assertThrows(InvalidURLException.class, () -> urlService.getUrl("https://shrt.com/a1b2c3z"));
        assertThrows(InvalidURLException.class, () -> urlService.getUrl("https://some.com/a1b2c3"));
        assertThrows(InvalidURLException.class, () -> urlService.getUrl("https://shrt.com"));
        assertThrows(InvalidURLException.class, () -> urlService.getUrl("https://shrt.com/"));
    }

    @Test
    @DisplayName("Test parsing of short URL")
    void test_getUrl_parseHashFromShortURL() {
        String shortUrl = "https://shrt.com/123abc";
        hash = "123abc";

        when(urlCacheService.getFromCache(anyString())).thenReturn(Optional.of(anyString()));

        urlService.getUrl(shortUrl);

        verify(urlCacheService, times(1)).getFromCache(hash);

        assertThrows(InvalidURLException.class, () -> urlService.getUrl("notRealUrl"));
    }

    @Test
    @DisplayName("Test get real URL from cache success")
    void test_getUrl_WhenValidInput_ReturnsFromCache() {
        String shortUrl = "https://shrt.com/abc";
        String longUrl = "https://google.com";

        when(urlCacheService.getFromCache(anyString())).thenReturn(Optional.of(longUrl));

        LongUrlDto result = urlService.getUrl(shortUrl);

        verify(urlCacheService, times(1)).getFromCache(anyString());
        verify(urlRepository, never()).findById(anyString());
        verify(urlCacheService, never()).saveToCache(anyString(), anyString());

        assertNotNull(result);
        assertEquals(longUrl, result.url());
    }

    @Test
    @DisplayName("Test get real URL from database")
    void test_getUrl_WhenValidInput_ReturnsFromDB() {
        String shortUrl = "https://shrt.com/abc";
        String longUrl = "https://google.com";
        ShortUrl urlEntity = ShortUrl.builder()
                .url(longUrl)
                .build();

        when(urlCacheService.getFromCache(anyString())).thenReturn(Optional.empty());
        when(urlRepository.findById(anyString())).thenReturn(Optional.of(urlEntity));

        LongUrlDto result = urlService.getUrl(shortUrl);

        verify(urlCacheService, times(1)).getFromCache(anyString());
        verify(urlRepository, times(1)).findById(anyString());
        verify(urlCacheService, times(1)).saveToCache(anyString(), anyString());

        assertNotNull(result);
        assertEquals(longUrl, result.url());
    }

    @Test
    @DisplayName("Test get real URL from database fail - no such url in DB")
    void test_getUrl_WhenInvalidInput_ReturnsException() {
        String shortUrl = "https://shrt.com/abc";

        when(urlCacheService.getFromCache(anyString())).thenReturn(Optional.empty());
        when(urlRepository.findById(anyString())).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> urlService.getUrl(shortUrl));
        assertEquals("URL matching hash abc not found", ex.getMessage());

        verify(urlCacheService, times(1)).getFromCache(anyString());
        verify(urlRepository, times(1)).findById(anyString());
        verify(urlCacheService, never()).saveToCache(anyString(), anyString());
    }
}