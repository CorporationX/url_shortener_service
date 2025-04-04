package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlCreateDto;
import faang.school.urlshortenerservice.dto.UrlReadDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlControllerTest {

    @Mock
    private UrlService urlService;

    @InjectMocks
    private UrlController urlController;

    @Test
    void createShortUrl_ValidRequest_ReturnsCreated() {
        UrlCreateDto requestDto = new UrlCreateDto();
        requestDto.setOriginalUrl("https://example.com");

        UrlReadDto responseDto = new UrlReadDto();
        responseDto.setHash("abc123");
        responseDto.setOriginalUrl("https://example.com");
        responseDto.setCreatedAt(LocalDateTime.now());

        when(urlService.createShortUrl(requestDto)).thenReturn(responseDto);

        UrlReadDto result = urlController.createdShortUrl(requestDto);

        assertNotNull(result);
        assertEquals(responseDto.getHash(), result.getHash());
        assertEquals(responseDto.getOriginalUrl(), result.getOriginalUrl());
    }

    @Test
    void redirectLongUrl_ValidHash_ReturnsRedirect() {
        String hash = "validHash";
        String originalUrl = "https://example.com";

        when(urlService.getOriginalUrl(hash)).thenReturn(originalUrl);

        ModelAndView result = urlController.redirectLongUrl(hash);

        assertEquals("redirect:" + originalUrl, result.getViewName());
    }

    @Test
    void redirectLongUrl_NonExistentHash_ReturnsNotFound() {
        String nonExistentHash = "nonexistent";

        when(urlService.getOriginalUrl(nonExistentHash))
                .thenThrow(new EntityNotFoundException("URL not found"));

        assertThrows(EntityNotFoundException.class, () -> {
            urlController.redirectLongUrl(nonExistentHash);
        });
    }
}
