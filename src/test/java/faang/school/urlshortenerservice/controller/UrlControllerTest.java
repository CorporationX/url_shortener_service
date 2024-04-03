package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDtoRequest;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.view.RedirectView;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UrlControllerTest {
    @Mock
    private UrlService urlService;
    @InjectMocks
    private UrlController urlController;
    String inUrl;
    String outUrl;

    @BeforeEach
    public void setup() {
        inUrl = "http://habrahabr.ru/eydtjyekuk576wsy4wywh6835?56";
        outUrl = "http://localhost:8080";
    }

    @Test
    public void testGetUrlWhenValidRequest() {
        UrlDtoRequest request = new UrlDtoRequest(inUrl);
        String hash = "abc123";
        String fullUrlWithHash = outUrl + "/" + hash;
        when(urlService.createShortUrl(request.url())).thenReturn(hash);

        urlController.getUrl(request);
        verify(urlService, times(1)).createShortUrl(request.url());
    }

    @Test
    public void testRedirectToUrlWhenValidRequestThenRedirectToCorrectUrl() {
        String hash = "abc123";
        String url = "http://example.com";
        when(urlService.getUrlByHash(hash)).thenReturn(url);

        RedirectView redirectView = urlController.redirectToUrl(hash);
        assertEquals(url, redirectView.getUrl());
        verify(urlService, times(1)).getUrlByHash(hash);
    }
}