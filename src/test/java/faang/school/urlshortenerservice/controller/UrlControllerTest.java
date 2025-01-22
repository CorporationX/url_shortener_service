package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.config.context.UserContext;
import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.util.UrlRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UrlController.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlService urlService;

    @MockBean
    private UserContext userContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getOriginalUrl_ShouldReturnFoundStatusWithLocationHeader() throws Exception {
        String hash = "abc123";
        String originalUrl = "http://example.com";
        long userId = 123L;
        when(userContext.getUserId()).thenReturn(userId);
        when(urlService.getOriginalUrl(hash, userId)).thenReturn(originalUrl);

        mockMvc.perform(get("/{hash}", hash)
                .header("x-user-id", userId))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", originalUrl));

        verify(urlService, times(1)).getOriginalUrl(hash, userId);
    }

    @Test
    void getOriginalUrl_ShouldReturnNotFound_WhenHashNotExists() throws Exception {
        String hash = "invalidHash";
        long userId = 123L;
        when(userContext.getUserId()).thenReturn(userId);
        when(urlService.getOriginalUrl(hash, userId)).thenThrow(new IllegalArgumentException("Hash not found"));

        mockMvc.perform(get("/{hash}", hash)
                        .header("x-user-id", userId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Hash not found"));

        verify(urlService, times(1)).getOriginalUrl(hash, userId);
    }

    @Test
    void createShortUrl_ShouldReturnShortUrl() throws Exception {
        String originalUrl = "http://example.com";
        String shortUrl = "http://short.ly/abc123";
        long userId = 123L;
        UrlRequest urlRequest = new UrlRequest(originalUrl);
        when(userContext.getUserId()).thenReturn(userId);
        when(urlService.createShortUrl(originalUrl, userId)).thenReturn(shortUrl);

        mockMvc.perform(post("/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", userId)
                        .content(objectMapper.writeValueAsString(urlRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(shortUrl));

        verify(urlService, times(1)).createShortUrl(originalUrl, userId);
    }

    @Test
    void createShortUrl_ShouldReturnBadRequest_WhenRequestBodyIsInvalid() throws Exception {
        String invalidRequest = "{}";
        long userId = 123L;

        mockMvc.perform(post("/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", userId)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.url").value("URL must not be blank"));

        verify(urlService, never()).createShortUrl(anyString(), anyLong());
    }
}
