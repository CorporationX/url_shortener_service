package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.config.context.UserContext;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlController.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlService urlService;

    @MockBean
    private UserContext userContext;

    @Test
    void createShortUrl_ShouldReturnShortenedUrl() throws Exception {
        String shortenedUrl = "http://short.ly/abc123";
        when(urlService.shortenUrl(any(UrlDto.class))).thenReturn(shortenedUrl);

        mockMvc.perform(post("/shortener/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\": \"http://example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(shortenedUrl));

        verify(urlService, times(1)).shortenUrl(any(UrlDto.class));
    }

    @Test
    void redirectToOriginalUrl_ShouldRedirectToOriginalUrl() throws Exception {
        String hash = "abc123";
        String originalUrl = "http://example.com";
        when(urlService.getOriginalUrl(eq(hash))).thenReturn(originalUrl);

        mockMvc.perform(get("/shortener/{hash}", hash))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", originalUrl));

        verify(urlService, times(1)).getOriginalUrl(hash);
    }
}
