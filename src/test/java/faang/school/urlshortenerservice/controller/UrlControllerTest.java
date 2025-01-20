package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.handler.UrlExceptionHandler;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {UrlController.class})
@Import(UrlExceptionHandler.class)
public class UrlControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UrlService urlService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String POST_URL = "/v1/url/shortener";
    private static final String GET_URL = "/v1/url/{hash}";

    @Test
    public void testCreateShortUrl() throws Exception {
        String url = "https://example.com";
        UrlRequestDto requestDto = new UrlRequestDto(url);

        UrlResponseDto responseDto = new UrlResponseDto();
        String hash = "abc123";
        responseDto.setHash(hash);
        responseDto.setUrl(url);

        Mockito.when(urlService.processUrl(requestDto.getUrl())).thenReturn(responseDto);
        mockMvc.perform(post(POST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.hash").value(hash))
                .andExpect(jsonPath("$.url").value(url));

        Mockito.verify(urlService, Mockito.times(1)).processUrl(url);
    }

    @Test
    public void testCreateShortUrlAlreadyExists() throws Exception {
        UrlRequestDto requestDto = new UrlRequestDto();
        String url = "https://existing-url.com";
        requestDto.setUrl(url);
        Mockito.when(urlService.processUrl(requestDto.getUrl()))
                .thenThrow(new EntityExistsException("URL already exists"));

        mockMvc.perform(post(POST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("URL already exists"));

        Mockito.verify(urlService, Mockito.times(1)).processUrl(url);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidUrls")
    public void testCreateShortUrlInvalidInput(String invalidUrl) throws Exception {
        UrlRequestDto invalidRequestDto = new UrlRequestDto();
        invalidRequestDto.setUrl(invalidUrl);

        mockMvc.perform(post(POST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());

        Mockito.verify(urlService, Mockito.times(0)).processUrl(invalidUrl);
    }

    private static Stream<String> provideInvalidUrls() {
        return Stream.of("invalid-url",
                "http://",
                "https://.",
                "http://invalid-.com",
                "https://invalid.com:",
                "",
                null);
    }

    @Test
    public void testRedirectToUrl() throws Exception {
        String hash = "abc123";
        String longUrl = "http://example.com";
        Mockito.when(urlService.getUrlByHash(hash)).thenReturn(longUrl);

        mockMvc.perform(MockMvcRequestBuilders.get(GET_URL, hash))
                .andExpect(status().isFound())
                .andExpect(MockMvcResultMatchers.header().string("Location", longUrl));

        Mockito.verify(urlService, Mockito.times(1)).getUrlByHash(hash);
    }

    @Test
    public void testRedirectToUrlNotFound() throws Exception {
        String hash = "nonExistingHash";

        Mockito.when(urlService.getUrlByHash(eq(hash))).thenThrow(new EntityNotFoundException("URL not found for hash: " + hash));

        mockMvc.perform(MockMvcRequestBuilders.get(GET_URL, hash))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("URL not found for hash: nonExistingHash"));

        Mockito.verify(urlService, Mockito.times(1)).getUrlByHash(hash);
    }
}
