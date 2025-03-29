package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.util.UriBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UrlController.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlService urlService;

    @MockBean
    private UriBuilder uriBuilder;

    @Test
    @DisplayName("POST /url - success")
    void testCreateShortUrl_success() throws Exception {
        String originalUrl = "https://google.com";
        String hash = "abc123";
        String responseUrl = "http://short/abc123";

        when(urlService.createShortUrl(originalUrl)).thenReturn(hash);
        when(uriBuilder.response(hash)).thenReturn(responseUrl);

        mockMvc.perform(post("/url").param("url", originalUrl))
                .andExpect(status().isOk())
                .andExpect(content().string(responseUrl));
    }

    @Test
    @DisplayName("POST /url - invalid URL")
    void testCreateShortUrl_invalidUrl() throws Exception {
        mockMvc.perform(post("/url").param("url", "not-a-valid-url"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /{hash} - success")
    void testRedirect_success() throws Exception {
        String hash = "abc123";
        String targetUrl = "https://google.com";

        when(urlService.getOriginalUrl(hash)).thenReturn(targetUrl);

        mockMvc.perform(get("/" + hash))
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, targetUrl));
    }

    @Test
    @DisplayName("GET /{hash} - not found")
    void testRedirect_notFound() throws Exception {
        String hash = "xyz789";
        when(urlService.getOriginalUrl(hash)).thenThrow(new RuntimeException("not found"));

        mockMvc.perform(get("/" + hash))
                .andExpect(status().isInternalServerError());
    }
}