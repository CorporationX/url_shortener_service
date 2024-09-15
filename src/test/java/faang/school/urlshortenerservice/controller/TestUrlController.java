package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestUrlController {
    @Mock
    private UrlService urlService;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private UrlController urlController;

    @Test
    public void redirectToOriginalUrlTest(){
        String hash = "o9";
        UrlDto urlDto = new UrlDto();
        urlDto.setUrl(hash);
        when(urlService.findUrl(hash)).thenReturn(urlDto);
        urlController.redirectToOriginalUrl(hash);
        verify(urlService,times(1)).findUrl(hash);
    }

    @Test public void createShortUrlTest(){
        String hash = "o9";
        UrlDto urlDto = new UrlDto();
        urlDto.setUrl(hash);
        urlController.createShortUrl(urlDto);
        verify(urlService).convertToShortUrl(urlDto);
    }
}
