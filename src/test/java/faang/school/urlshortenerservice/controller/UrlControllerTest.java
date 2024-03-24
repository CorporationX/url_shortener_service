package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UrlControllerTest {
    @Mock
    private UrlService urlService;

    @InjectMocks
    private UrlController urlController;

    @Test
    void getShortUrlTest() {
        String url = "https://www.google.com";
        String shortUrl = "abc123";
        Mockito.when(urlService.getShortUrl(url)).thenReturn(shortUrl);
        assertEquals(shortUrl, urlController.getShortUrl(new UrlDto(url)));
    }
}