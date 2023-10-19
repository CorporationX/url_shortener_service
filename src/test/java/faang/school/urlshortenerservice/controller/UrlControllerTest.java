package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDTO;
import faang.school.urlshortenerservice.exception.url.InvalidUrlException;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class UrlControllerTest {
    @InjectMocks
    private UrlController urlController;

    @Mock
    private UrlService urlService;

    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        response = new MockHttpServletResponse();

        ReflectionTestUtils.setField(urlController, "serverAddress", "https://sh.c/");

    }

    @Test
    void testShortenUrl_ValidUrl() {
        UrlDTO urlDTO = new UrlDTO("https://example.com", LocalDateTime.now());

        when(urlService.shortenUrl(urlDTO.getUrl())).thenReturn("shortened-url");

        ResponseEntity<String> response = urlController.shortenUrl(urlDTO);

        verify(urlService, times(1)).shortenUrl(urlDTO.getUrl());
        assertEquals("shortened-url", response.getBody());
    }


    @Test
    void testShortenUrl_InvalidUrl2() {
        UrlDTO requestDTO = new UrlDTO("invalid_url", LocalDateTime.now());

        assertThrows(InvalidUrlException.class, () -> urlController.shortenUrl(requestDTO));
    }

    @Test
    void testRedirectToOriginalURL_Found() {
        when(urlService.getOriginalURL("https://sh.c/abc123")).thenReturn("https://example.com");

        urlController.redirectToOriginalURL("abc123", response);

        verify(urlService, times(1)).getOriginalURL("https://sh.c/abc123");
        assertEquals(302, response.getStatus());
        assertEquals("https://example.com", response.getHeader("Location"));
    }

    @Test
    void testRedirectToOriginalURL_NotFound() {
        when(urlService.getOriginalURL("https://sh.c/nonExistentHash")).thenReturn(null);

        urlController.redirectToOriginalURL("nonExistentHash", response);

        verify(urlService, times(1)).getOriginalURL("https://sh.c/nonExistentHash");
        assertEquals(404, response.getStatus());
    }
}