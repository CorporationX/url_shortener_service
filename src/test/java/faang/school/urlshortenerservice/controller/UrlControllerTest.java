package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.view.RedirectView;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlControllerTest {

    @Mock
    private UrlService urlService;

    @InjectMocks
    private UrlController urlController;

    private UrlDto urlDto;
    private final String longUrl = "http://example.com/very/long/url";
    private final String shortUrl = "http://short.url/abc123";
    private final String hash = "abc123";

    @BeforeEach
    void setUp() {
        urlDto = new UrlDto();
        urlDto.setUrl(longUrl);
    }

    @Test
    void createShortUrl_ReturnRedirectView() {
        when(urlService.createShortUrl(longUrl)).thenReturn(shortUrl);
        RedirectView result = urlController.createShortUrl(urlDto);

        assertEquals(shortUrl, result.getUrl());
    }

    @Test
    void createLongUrl_ReturnRedirectView() {
        when(urlService.getLongUrl(hash)).thenReturn(longUrl);
        RedirectView result = urlController.createLongUrl(hash);

        assertEquals(longUrl, result.getUrl());
    }
}
