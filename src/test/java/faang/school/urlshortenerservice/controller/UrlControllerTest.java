package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.OriginalUrlRequest;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UrlControllerTest {
    @Mock
    private UrlService urlService;
    @InjectMocks
    private UrlController urlController;

    @Test
    public void testRedirectByHash() {
        String testHash = "hash";
        urlController.redirectByHash(testHash);

        verify(urlService).getUrlByHash(testHash);
    }

    @Test
    public void testCreateShortUrl() {
        OriginalUrlRequest request = new OriginalUrlRequest("url");
        urlController.createShortUrl(request);

        verify(urlService).createShortUrl(request);
    }
}