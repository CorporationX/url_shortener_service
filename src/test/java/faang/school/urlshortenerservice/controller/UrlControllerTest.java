package faang.school.urlshortenerservice.controller;

import faang.school.urlshorterservice.service.UrlService;
import org.junit.jupiter.api.extension.ExtendWith;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.dto.CreateUrlDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class UrlControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private UrlController urlController;

    @Mock
    private UrlService urlService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(urlController).build();
    }

    @Test
    void createShortUrl_shouldReturnShortenedUrl() throws Exception {
        CreateUrlDto createUrlDto = new CreateUrlDto("https://example.com/long-url");
        UrlResponseDto expectedResponse = new UrlResponseDto("https://short.url/abc123");

        when(urlService.createShortUrl(createUrlDto.getUrl())).thenReturn(expectedResponse.getShortUrl());

        mockMvc.perform(post("/api/v1/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUrlDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortUrl").value(expectedResponse.getShortUrl()));
    }
}