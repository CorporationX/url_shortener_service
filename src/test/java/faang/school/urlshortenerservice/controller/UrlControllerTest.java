package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.CreateShortUrlDto;
import faang.school.urlshortenerservice.dto.UrlViewDto;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;

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
    void testRedirect_Success() {
        String hash = "abc123";
        String originalUrl = "https://example.com";
        when(urlService.getOriginalUrl(hash)).thenReturn(originalUrl);

        ResponseEntity<Void> response = urlController.redirect(hash);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(URI.create(originalUrl), response.getHeaders().getLocation());
        verify(urlService).getOriginalUrl(hash);
    }

    @Test
    void testCreateShortUrl_Success() {
        CreateShortUrlDto requestDto = new CreateShortUrlDto("https://example.com");
        UrlViewDto expectedResponse = new UrlViewDto("https://short.com/abc123");

        when(urlService.createShortUrl(requestDto)).thenReturn(expectedResponse);

        ResponseEntity<UrlViewDto> response = urlController.createShortUrl(requestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(urlService).createShortUrl(requestDto);
    }

    @Test
    void testCreateShortUrl_WithValidUrl_ReturnsShortUrl() {
        CreateShortUrlDto requestDto = new CreateShortUrlDto("https://valid-url.com");
        UrlViewDto expectedResponse = new UrlViewDto("https://short.com/xyz789");

        when(urlService.createShortUrl(requestDto)).thenReturn(expectedResponse);

        ResponseEntity<UrlViewDto> response = urlController.createShortUrl(requestDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void testRedirect_WithDifferentHash_ReturnsCorrectLocation() {
        String hash = "differentHash";
        String originalUrl = "https://google.com";
        when(urlService.getOriginalUrl(hash)).thenReturn(originalUrl);

        ResponseEntity<Void> response = urlController.redirect(hash);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(URI.create(originalUrl), response.getHeaders().getLocation());
    }

    @Test
    void testCreateShortUrl_WithLongUrl_ReturnsCreated() {
        String longUrl = "https://very-long-url.com/path/to/resource?param=value";
        CreateShortUrlDto requestDto = new CreateShortUrlDto(longUrl);
        UrlViewDto expectedResponse = new UrlViewDto("https://short.com/short123");

        when(urlService.createShortUrl(requestDto)).thenReturn(expectedResponse);

        ResponseEntity<UrlViewDto> response = urlController.createShortUrl(requestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("https://short.com/short123", response.getBody().shortUrl());
    }
}