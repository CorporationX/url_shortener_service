package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UrlController.class)
@AutoConfigureMockMvc
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UrlService urlService;

    @Test
    void testCreateShortUrl() throws Exception {
        String originalUrl = "http://example.com";
        String shortUrl = "abc123";

        when(urlService.createShortUrl(originalUrl)).thenReturn(shortUrl);

        mockMvc.perform(post("/api/shorten")
                        .param("url", originalUrl))
                .andExpect(status().isOk())
                .andExpect(content().string(shortUrl));
    }

    @Test
    void testRedirectToUrl() throws Exception {
        String shortUrl = "abc123";
        String originalUrl = "http://example.com";

        when(urlService.getOriginalUrl(shortUrl)).thenReturn(originalUrl);

        mockMvc.perform(get("/" + shortUrl))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(originalUrl));
    }
}
