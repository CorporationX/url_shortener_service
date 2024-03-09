package faang.school.urlshortenerservice.controller;

import static org.mockito.Mockito.*;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UrlControllerTest {
    @InjectMocks
    private UrlController urlController;
    @Mock
    private UrlService urlService;

    @Test
    void testCreateShortUrl() {
        UrlDto urlDto = new UrlDto("url");
        urlController.createShortUrl(urlDto);
        verify(urlService, times(1)).createShortUrl(urlDto);
    }
}
