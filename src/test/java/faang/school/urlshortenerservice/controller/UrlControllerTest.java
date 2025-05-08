package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.config.context.UserContext;
import faang.school.urlshortenerservice.exception.DataNotFoundException;
import faang.school.urlshortenerservice.model.dto.UrlDto;
import faang.school.urlshortenerservice.service.interfaces.UrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
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
    void testCreateShortUrl_ValidUrlDto_ReturnsShortUrl() throws Exception {
        UrlDto urlDto = new UrlDto("https://example.com");
        String shortUrl = "http://short.url/abc123";

        when(urlService.getShortUrl(any(UrlDto.class))).thenReturn(shortUrl);

        mockMvc.perform(post("/api/v1/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(urlDto))
                        .header("x-user-id", 1))
                .andExpect(status().isCreated())
                .andExpect(content().string(shortUrl));
    }

    @Test
    void testCreateShortUrl_InvalidUrlDto_ReturnsBadRequest() throws Exception {
        UrlDto urlDto = new UrlDto("httttp");

        mockMvc.perform(post("/api/v1/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(urlDto))
                        .header("x-user-id", 1))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetOriginalUrl_ValidHash_RedirectsToOriginalUrl() throws Exception {
        String hash = "abc123";
        String originalUrl = "https://example.com";

        when(urlService.getOriginalUrl(hash)).thenReturn(originalUrl);

        mockMvc.perform(get("/api/v1/url/{hash}", hash)
                        .header("x-user-id", 1))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", originalUrl));
    }

    @Test
    void testGetOriginalUrl_InvalidHash_ReturnsInternalServerError() throws Exception {
        String hash = "invalidHash";
        String errorMessage = "Url with hash %s was not found in database".formatted(hash);

        when(urlService.getOriginalUrl(hash))
                .thenThrow(new DataNotFoundException(errorMessage));

        mockMvc.perform(get("/api/v1/url/{hash}", hash)
                        .header("x-user-id", 1))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(errorMessage));
    }
}
