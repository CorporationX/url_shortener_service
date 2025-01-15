package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.LongUrlDto;
import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.entity.ShortUrl;
import faang.school.urlshortenerservice.exception.InvalidURLException;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.HashCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    UrlRepository urlRepository;

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

        when(hashCache.getShortUrlFromCache()).thenReturn(hash);

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
    @DisplayName("Test short URL creation success")
    void test_createShortUrl_success() {
        when(hashCache.getShortUrlFromCache()).thenReturn(hash);

        ShortUrlDto result = urlService.createShortUrl(longUrlDto);

        verify(urlRepository, times(1)).save(any(ShortUrl.class));

        assertNotNull(result);
        assertEquals("http:/shrt.com/123abc", result.shortUrl());
    }
}