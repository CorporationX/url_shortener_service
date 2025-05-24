package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.dto.ShortUrlRequestDto;
import faang.school.urlshortenerservice.exception.ShortUrlNotFoundException;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UrlControllerTest {

    @Autowired
    @SuppressWarnings("unused")
    private MockMvc mockMvc;

    @MockBean
    @SuppressWarnings("unused")
    private UrlService urlService;

    @Test
    void whenValidUrl_thenReturnShortUrl() throws Exception {
        // Arrange
        var originalUrl = "https://example.com/some/long/path";
        var expectedShortUrl = "http://short.url/abc123";
        var requestDto = new ShortUrlRequestDto(originalUrl);

        when(urlService.getShortUrl(any(ShortUrlRequestDto.class))).thenReturn(expectedShortUrl);

        // Act
        var result = mockMvc.perform(post("/v1/url")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDto)));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(content().string(expectedShortUrl));
    }

    @Test
    void whenInvalidUrl_thenReturnBadRequest() throws Exception {
        // Arrange
        var invalidUrl = "not-a-url";
        var requestDto = new ShortUrlRequestDto(invalidUrl);
        var result = mockMvc.perform(post("/v1/url")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDto)));

        result.andExpect(status().isBadRequest());
    }

    @Test
    void whenEmptyUrl_thenReturnBadRequest() throws Exception {
        var requestDto = new ShortUrlRequestDto("");
        var result = mockMvc.perform(post("/v1/url")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDto)));

        result.andExpect(status().isBadRequest());
    }

    @Test
    void whenNullUrl_thenReturnBadRequest() throws Exception {
        var requestDto = new ShortUrlRequestDto(null);
        var result = mockMvc.perform(post("/v1/url")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDto)));

        result.andExpect(status().isBadRequest());
    }

    @Test
    public void testRedirectToOriginalUrl() throws Exception {
        // Arrange
        var testHash = "abc123";
        var originalUrl = "https://example.com";
        when(urlService.redirectToOriginalUrl(testHash)).thenReturn(originalUrl);

        mockMvc.perform(get("/v1/" + testHash))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", originalUrl));
    }

    @Test
    public void testRedirectWithNonExistentHash() throws Exception {
        var nonExistentHash = "nonexistent";
        when(urlService.redirectToOriginalUrl(nonExistentHash))
                .thenThrow(new ShortUrlNotFoundException("Hash not found"));

        mockMvc.perform(get("/v1/" + nonExistentHash))
                .andExpect(status().isBadRequest());
    }
}