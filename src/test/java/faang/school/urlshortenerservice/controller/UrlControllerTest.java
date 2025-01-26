package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.config.context.UserContext;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.utilities.UrlUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
    private UserContext userContext;

    @Test
    void createShortUrlSuccessTest() throws Exception {
        String shortenedUrl = "http://shortly-az/abc123";
        when(urlService.createShortUrl(any(UrlDto.class))).thenReturn(shortenedUrl);

        mockMvc.perform(post(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.url)
                        .header("x-user-id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\": \"http://example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(shortenedUrl));

        verify(urlService, times(1)).createShortUrl(any(UrlDto.class));
    }

    @Test
    void redirectSuccessTest() throws Exception {

        String hash = "abc123";
        String originalUrl = "http://example.com";
        when(urlService.getOriginalUrl(eq(hash))).thenReturn(originalUrl);

        mockMvc.perform(get(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.hash, hash)
                        .header("x-user-id", "1"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", originalUrl));

        verify(urlService, times(1)).getOriginalUrl(hash);
    }

    @Test
    void redirectFailTest() throws Exception {
        String hash = "nonexistent";

        when(urlService.getOriginalUrl(hash)).thenThrow(new UrlNotFoundException("Hash not found"));
        mockMvc.perform(get("/url/" + hash)
                        .header("x-user-id", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createShortUrlFailTest() throws Exception {
        String invalidUrl = "invalid";

        mockMvc.perform(post(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.url)
                        .header("x-user-id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"" + invalidUrl + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("URL is invalid"));
    }
}