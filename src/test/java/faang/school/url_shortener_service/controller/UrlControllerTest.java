package faang.school.url_shortener_service.controller;

import faang.school.url_shortener_service.dto.UrlRequestDto;
import faang.school.url_shortener_service.dto.UrlResponseDto;
import faang.school.url_shortener_service.exception.UrlExceptionHandler;
import faang.school.url_shortener_service.service.UrlService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UrlControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UrlService urlService;

    @InjectMocks
    private UrlController urlController;

    @Value("${short.url.base}")
    private String baseUrl;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        mockMvc = MockMvcBuilders.standaloneSetup(urlController)
                .setControllerAdvice(new UrlExceptionHandler())
                .build();
        baseUrl = "ShortUrl - ";
    }

    @Test
    void createShortURL_ShouldReturnShortenedUrl_WhenRequestIsValid() throws Exception {
        UrlResponseDto responseDto = new UrlResponseDto(baseUrl + "/abc123");
        when(urlService.createShortUrl(any(UrlRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"originalUrl\": \"https://example.com/long-url\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortUrl").value(baseUrl + "/abc123"));
    }

    @Test
    void createShortURL_ShouldReturnBadRequest_WhenUrlIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"originalUrl\": \"invalid-url\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void redirect_ShouldRedirectToOriginalUrl_WhenHashExists() throws Exception {
        String hash = "abc123";
        String originalUrl = "https://example.com/long-url";

        when(urlService.getOriginalURL(hash)).thenReturn(originalUrl);

        mockMvc.perform(get("/api/v1/url/{hash}", hash))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", originalUrl));
    }

    @Test
    void redirect_ShouldReturnNotFound_WhenHashDoesNotExist() throws Exception {
        String hash = "invalidHash";

        when(urlService.getOriginalURL(hash)).thenThrow(new EntityNotFoundException("URL not found"));

        mockMvc.perform(get("/api/v1/url/{hash}", hash))
                .andExpect(status().isNotFound());
    }
}