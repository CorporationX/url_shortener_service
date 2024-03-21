package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.RequestUrl;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlControllerTest {
    @Mock
    private UrlService urlService;
    @InjectMocks
    private UrlController urlController;

    @Test
    void testGetHash() {
        RequestUrl url = new RequestUrl();
        urlController.getHash(url);
        verify(urlService).getHash(url.getUrl());
    }

    @Test
    void testGetUrl() {
        RequestUrl url = new RequestUrl();
        when(urlService.getLongUrl(url.getUrl())).thenReturn("url");
        urlController.getUrl(url);
        verify(urlService).getLongUrl(url.getUrl());
    }
}