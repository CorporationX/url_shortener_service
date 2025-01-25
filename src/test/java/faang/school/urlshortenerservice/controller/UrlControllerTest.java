package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.DataNotFoundException;
import faang.school.urlshortenerservice.exception.UrlExceptionHandler;
import faang.school.urlshortenerservice.service.UrlService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UrlControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UrlService urlService;

    private String originalUrl;
    private String shortUrl;
    private UrlDto urlDto;
    private UrlDto nullUrlDto;
    private String hash;
    String invalidhash;

    @InjectMocks
    private UrlController urlController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(urlController)
                .setControllerAdvice(new UrlExceptionHandler()).build();
        objectMapper = new ObjectMapper();
        originalUrl = "http://example.com";
        shortUrl = "http://short.url/abc123";

        urlDto = UrlDto.builder()
                .url(originalUrl)
                .build();

        nullUrlDto = UrlDto.builder()
                .url("")
                .build();
        hash = "abc123";
        invalidhash = "invalidHash";
    }

    @Test
    void testCreateShotUrlSuccess() throws Exception {
        when(urlService.getShotUrl(any(UrlDto.class))).thenReturn(shortUrl);

        mockMvc.perform(post("/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(urlDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string(shortUrl));
    }

    @Test
    void testCreateShotUrlInvalidUrl() throws Exception {
        mockMvc.perform(post("/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nullUrlDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.url").value("URL can not be blank"));
    }

    @Test
    void testGetOriginalUrlSuccess() throws Exception {
        when(urlService.getOriginalUrl(anyString())).thenReturn(originalUrl);

        mockMvc.perform(get("/url/{shortUrl}", hash))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(originalUrl));
    }

    @Test
    void testGetOriginalUrlNotFound() throws Exception {
        when(urlService.getOriginalUrl(anyString())).thenThrow(new DataNotFoundException("URL not found"));

        mockMvc.perform(get("/url/{shortUrl}", invalidhash))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("URL not found"));
    }
}