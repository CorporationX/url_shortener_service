package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.validate.UrlValidate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlControllerTest {

    @InjectMocks
    private UrlController urlController;

    @Mock
    private UrlService service;

    @Mock
    private UrlValidate urlValidate;

    @Test
    void testgenerateShortUrl() {
        UrlDto urlDto = new UrlDto();
        urlDto.setOriginalUrl("https://www.now.ru");
        service.generateShortUrl(urlDto);
        verify(service, times(1)).generateShortUrl(urlDto);
    }

    @Test
    void testreturnFullUrl() {
        String hash = "rr";
        String redirectUrl = "https://www.now.ru";
        when(service.returnFullUrl(hash)).thenReturn(redirectUrl);
        service.returnFullUrl(hash);
        verify(service, times(1)).returnFullUrl(hash);
    }
}