package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UrlController.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UrlService urlService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createShortUrl_ValidInput_ReturnsCreated() throws Exception {
        String longUrl = "https://www.example.com";
        String shortUrl = "http://sh.c/abc123";
        UrlRequestDto requestDto = new UrlRequestDto(longUrl);

        when(urlService.shortenUrl(anyString())).thenReturn(shortUrl);

        mockMvc.perform(post("/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortUrl").value(shortUrl));
    }

    @Test
    void createShortUrl_InvalidInput_ReturnsBadRequest() throws Exception {
        UrlRequestDto requestDto = new UrlRequestDto("");

        mockMvc.perform(post("/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createShortUrl_NullInput_ReturnsBadRequest() throws Exception {
        UrlRequestDto requestDto = new UrlRequestDto(null);

        mockMvc.perform(post("/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }
}