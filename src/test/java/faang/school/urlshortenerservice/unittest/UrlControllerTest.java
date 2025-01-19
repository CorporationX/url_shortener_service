package faang.school.urlshortenerservice.unittest;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.config.context.UserContext;
import faang.school.urlshortenerservice.controller.UrlController;
import faang.school.urlshortenerservice.dto.UrlRequestDTO;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlController.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlService urlService;

    @MockBean
    private UserContext userContext; // Add mock for UserContext

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createShortUrl_ShouldReturnShortUrl() throws Exception {
        String longUrl = "https://example.com";
        String expectedShortUrl = "http://short.url/1234abcd";

        UrlRequestDTO urlRequestDTO = new UrlRequestDTO();
        urlRequestDTO.setLongUrl(longUrl);

        Mockito.when(userContext.getUserId()).thenReturn(123L); // Mock behavior
        Mockito.when(urlService.createShortUrl(any(String.class))).thenReturn(expectedShortUrl);

        mockMvc.perform(post("/url")
                        .header("x-user-id", "123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(urlRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedShortUrl));
    }

    @Test
    void createShortUrl_ShouldReturnBadRequestForInvalidUrl() throws Exception {
        UrlRequestDTO urlRequestDTO = new UrlRequestDTO();
        urlRequestDTO.setLongUrl("invalid_url"); // Does not match the URL pattern

        mockMvc.perform(post("/url")
                        .header("x-user-id", "123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(urlRequestDTO)))
                .andExpect(status().isBadRequest());
    }
}
