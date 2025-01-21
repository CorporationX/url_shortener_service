package faang.school.urlshortenerservice.controller;


import faang.school.urlshortenerservice.service.UrlService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlController.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlService urlService;

    @Test
    void shortenUrlValidUrlSuccessTest() throws Exception {
        String longUrl = "https://example.com";
        String shortUrl = "http://short.ly/abc123";

        when(urlService.createShortUrl(longUrl)).thenReturn(shortUrl);

        mockMvc.perform(post("/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"" + longUrl + "\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(shortUrl));
    }

    @Test
    void shortenUrl_invalidUrl_shouldReturnBadRequest() throws Exception {
        String invalidUrl = "invalid-url";

        mockMvc.perform(post("/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"" + invalidUrl + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid URL format."));
    }

    @Test
    void redirectToUrlExistingHashSuccessTest() throws Exception {
        String hash = "abc123";
        String longUrl = "https://example.com";

        when(urlService.getUrlByHash(hash)).thenReturn(longUrl);

        mockMvc.perform(get("/url/" + hash))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", longUrl));
    }

    @Test
    void redirectToUrlNonExistingHashFailTest() throws Exception {
        String hash = "nonexistent";

        when(urlService.getUrlByHash(hash)).thenThrow(new EntityNotFoundException("Hash not found"));
        mockMvc.perform(get("/url/" + hash))
                .andExpect(status().isNotFound());
    }
}
