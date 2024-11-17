package faang.school.urlshortenerservice.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import faang.school.urlshortenerservice.dto.Link;
import faang.school.urlshortenerservice.service.UrlService;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UrlControllerTest {

    @Mock
    private UrlService urlService;

    @InjectMocks
    private UrlController urlController;

    @Test
    void getComingUrlTest() {
        Link link= new Link("https://www.awessalom.com");
        String shortUrl = "apples";
        Mockito.when(urlService.createShortUrl(link)).thenReturn(shortUrl);
        assertEquals(shortUrl, urlController.comingUrl(link));
    }

    @Test
    void getCovertedUrlTest() {
        String shortUrl = "orange";
        String url = "https://www.test.com";
        Mockito.when(urlService.getShortUrl(shortUrl)).thenReturn(url);
        assertEquals(url, urlController.convertedUrl(shortUrl).getUrl());
    }
}