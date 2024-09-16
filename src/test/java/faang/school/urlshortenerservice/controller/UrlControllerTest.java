package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.dto.DtoValidationConstraints;
import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.exception.ExceptionMessages;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(DtoValidationConstraints.VALIDATION_FAILED))
                .andExpect(jsonPath("$.details").value(containsString(DtoValidationConstraints.EMPTY_URL)));
    }

    @Test
    void createShortUrl_InvalidEmail_ReturnsBadRequest() throws Exception {
        UrlRequestDto requestDto = new UrlRequestDto("google/com");

        mockMvc.perform(post("/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(DtoValidationConstraints.VALIDATION_FAILED))
                .andExpect(jsonPath("$.details").value(containsString(DtoValidationConstraints.INVALID_URL)));
    }

    @Test
    void redirectUrl_ValidHash_ReturnsRedirect() throws Exception {
        String hash = "abc123";
        String longUrl = "https://www.example.com";

        when(urlService.getLongUrl(hash)).thenReturn(longUrl);

        mockMvc.perform(get("/url/{hash}", hash))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", longUrl));
    }

    @Test
    void redirectUrl_InvalidHash_ReturnsNotFound() throws Exception {
        String hash = "invalid";

        when(urlService.getLongUrl(hash)).thenThrow(new UrlNotFoundException(String.format(ExceptionMessages.URL_NOT_FOUND, hash)));

        mockMvc.perform(get("/url/{hash}", hash))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString(hash)));
    }
}