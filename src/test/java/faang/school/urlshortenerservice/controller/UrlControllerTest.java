//package faang.school.urlshortenerservice.controller;
//
//import faang.school.urlshortenerservice.dto.UrlDTO;
//import faang.school.urlshortenerservice.exception.url.InvalidUrlException;
//import faang.school.urlshortenerservice.service.UrlService;
//import jakarta.servlet.http.HttpServletResponse;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.mock.web.MockHttpServletResponse;
//import java.time.LocalDateTime;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class UrlControllerTest {
//    @InjectMocks
//    private UrlController urlController;
//
//    @Mock
//    private UrlService urlService;
//
//    private MockHttpServletResponse response;
//
//    @BeforeEach
//    void setUp() {
//        response = new MockHttpServletResponse();
//    }
//
//    @Test
//    void testShortenUrl_ValidUrl() {
//        UrlDTO requestDTO = new UrlDTO("https://example.com", LocalDateTime.now());
//        String shortenedUrl = "https://sh.c/abc123";
//
//        when(urlService.shortenUrl(any(String.class))).thenReturn(shortenedUrl);
//
//        String result = urlController.shortenUrl(requestDTO);
//        assertEquals(shortenedUrl, result);
//    }
//
//    @Test
//    void testShortenUrl_InvalidUrl() {
//        UrlDTO requestDTO = new UrlDTO("invalid_url", LocalDateTime.now());
//
//        assertThrows(InvalidUrlException.class, () -> urlController.shortenUrl(requestDTO));
//    }
//
//    @Test
//    void testRedirectToOriginalURL_ExistingUrl() {
//        String hash = "abc123";
//        String originalUrl = "https://example.com";
//
//        when(urlService.getOriginalURL(hash)).thenReturn(originalUrl);
//
//        urlController.redirectToOriginalURL(hash, response);
//
//        assertEquals(HttpServletResponse.SC_FOUND, response.getStatus());
//        assertEquals(originalUrl, response.getHeader("Location"));
//    }
//
//    @Test
//    void testRedirectToOriginalURL_NonExistingUrl() {
//        String hash = "nonexistent_hash";
//
//        when(urlService.getOriginalURL(hash)).thenReturn(null);
//
//        urlController.redirectToOriginalURL(hash, response);
//
//        assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
//    }
//}