package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.config.context.UserContext;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UrlController.class)
@AutoConfigureMockMvc
public class UrlControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlServiceImpl urlShortenerService;
    @MockBean
    private UserContext userContext;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void shouldReturnShortenedUrl() throws Exception {

        UrlDto mockResponse = new UrlDto();
        mockResponse.setUrl(getUrl());
        mockResponse.setShortUrl(getShortUrl());

        when(urlShortenerService.shortenUrl(any(UrlDto.class))).thenReturn(mockResponse);

        UrlDto requestDto = new UrlDto();
        requestDto.setUrl(getUrl());

        mockMvc.perform(post("/api/v1/urls/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", "2")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value(getUrl()))
                .andExpect(jsonPath("$.shortUrl").value(getShortUrl()));
    }

    @Test
    void shouldReturnBadRequestForInvalidUrl() throws Exception {

        UrlDto requestDto = new UrlDto();
        requestDto.setUrl("");

        mockMvc.perform(post("/api/v1/urls/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", "2")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    private String getShortUrl() {
        return "http://short.ly/example";
    }

    private String getUrl() {
        return "http://example.com";
    }
}
