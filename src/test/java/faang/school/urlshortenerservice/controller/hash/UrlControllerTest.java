package faang.school.urlshortenerservice.controller.hash;

import faang.school.urlshortenerservice.dto.hash.HashDto;
import faang.school.urlshortenerservice.dto.url.UrlDto;
import faang.school.urlshortenerservice.service.url.UrlService;
import faang.school.urlshortenerservice.validator.url.UrlValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.view.RedirectView;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlControllerTest {

    @InjectMocks
    private UrlController urlController;

    @Mock
    private UrlService urlService;

    @Mock
    private UrlValidator urlValidator;

    private String hash;
    private String url;
    private HashDto hashDto;
    private UrlDto urlDto;

    @BeforeEach
    public void setUp() {
        hash = "abc123";
        url = "http://example.com";
        hashDto = new HashDto(hash);
        urlDto = new UrlDto(url);
    }

    @Test
    public void testTransformUrlToHash() {
        doNothing().when(urlValidator).validateUrl(url);
        when(urlService.transformUrlToHash(url)).thenReturn(hash);

        String result = urlController.transformUrlToHash(urlDto);

        verify(urlValidator, times(1)).validateUrl(url);
        verify(urlService, times(1)).transformUrlToHash(url);
        assertEquals(hash, result);
    }

    @Test
    public void testRedirect() {
        when(urlService.getUrlFromHash(hash)).thenReturn(url);

        RedirectView result = urlController.redirect(hashDto);

        verify(urlService, times(1)).getUrlFromHash(hash);
        assertEquals(url, result.getUrl());
    }
}
