package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.config.context.UserHeaderFilter;
import faang.school.urlshortenerservice.dto.ShortUrlResponse;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = UrlController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = UserHeaderFilter.class
        )
)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UrlService urlService;

    private static final String TEST_URL = "https://example.com/very/long/url";
    private static final String TEST_HASH = "abc123";
    private static final String SHORT_URL = "https://short.com/abc123";

    @Test
    void testCreateShortUrlSuccess() throws Exception {
        // Given
        UrlDto urlDto = UrlDto.builder()
                .url(TEST_URL)
                .build();

        ShortUrlResponse response = ShortUrlResponse.builder()
                .shortUrl(SHORT_URL)
                .hash(TEST_HASH)
                .build();

        when(urlService.createShortUrl(TEST_URL)).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(urlDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortUrl").value(SHORT_URL))
                .andExpect(jsonPath("$.hash").value(TEST_HASH));

        verify(urlService, times(1)).createShortUrl(TEST_URL);
    }

    @Test
    void testCreateShortUrlWithInvalidUrl() throws Exception {
        // Given
        UrlDto urlDto = UrlDto.builder()
                .url("not-a-valid-url")
                .build();

        // When & Then
        mockMvc.perform(post("/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(urlDto)))
                .andExpect(status().isBadRequest());

        verify(urlService, never()).createShortUrl(anyString());
    }

    @Test
    void testCreateShortUrlWithBlankUrl() throws Exception {
        // Given
        UrlDto urlDto = UrlDto.builder()
                .url("")
                .build();

        // When & Then
        mockMvc.perform(post("/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(urlDto)))
                .andExpect(status().isBadRequest());

        verify(urlService, never()).createShortUrl(anyString());
    }

    @Test
    void testCreateShortUrlWithNullUrl() throws Exception {
        // Given
        UrlDto urlDto = UrlDto.builder()
                .url(null)
                .build();

        // When & Then
        mockMvc.perform(post("/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(urlDto)))
                .andExpect(status().isBadRequest());

        verify(urlService, never()).createShortUrl(anyString());
    }

    @Test
    void testRedirectToOriginalUrlSuccess() throws Exception {
        // Given
        when(urlService.getOriginalUrl(TEST_HASH)).thenReturn(TEST_URL);

        // When & Then
        mockMvc.perform(get("/url/{hash}", TEST_HASH))
                .andExpect(status().isFound()) // 302
                .andExpect(redirectedUrl(TEST_URL));

        verify(urlService, times(1)).getOriginalUrl(TEST_HASH);
    }

    @Test
    void testRedirectToOriginalUrlNotFound() throws Exception {
        // Given
        when(urlService.getOriginalUrl(TEST_HASH))
                .thenThrow(new UrlNotFoundException("URL not found for hash: " + TEST_HASH));

        // When & Then
        mockMvc.perform(get("/url/{hash}", TEST_HASH))
                .andExpect(status().isNotFound());

        verify(urlService, times(1)).getOriginalUrl(TEST_HASH);
    }

    @Test
    void testCreateShortUrlWithValidHttpUrl() throws Exception {
        // Given
        UrlDto urlDto = UrlDto.builder()
                .url("http://example.com/path")
                .build();

        ShortUrlResponse response = ShortUrlResponse.builder()
                .shortUrl(SHORT_URL)
                .hash(TEST_HASH)
                .build();

        when(urlService.createShortUrl(anyString())).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(urlDto)))
                .andExpect(status().isCreated());

        verify(urlService, times(1)).createShortUrl("http://example.com/path");
    }

    @Test
    void testCreateShortUrlWithComplexUrl() throws Exception {
        // Given
        String complexUrl = "https://example.com/path?param1=value1&param2=value2#section";
        UrlDto urlDto = UrlDto.builder()
                .url(complexUrl)
                .build();

        ShortUrlResponse response = ShortUrlResponse.builder()
                .shortUrl(SHORT_URL)
                .hash(TEST_HASH)
                .build();

        when(urlService.createShortUrl(complexUrl)).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(urlDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortUrl").value(SHORT_URL));

        verify(urlService, times(1)).createShortUrl(complexUrl);
    }
}