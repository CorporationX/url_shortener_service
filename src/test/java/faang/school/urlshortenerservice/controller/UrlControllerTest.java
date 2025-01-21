package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.stream.Stream;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = UrlController.class)
public class UrlControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    UrlService urlService;
    private final static String BASE_URL = "/url-shortener";
    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    public void testGetUrl_RedirectSuccess() throws Exception {
        String testUrl = BASE_URL + "/{hash}";
        String testHash = "testHash";
        String testOriginalUrl = "http://example.com";

        when(urlService.getOriginalUrl(testHash)).thenReturn(testOriginalUrl);

        mockMvc.perform(get(testUrl, testHash)
                        .header("x-user-id", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(testOriginalUrl));

        verify(urlService, times(1)).getOriginalUrl(testHash);
    }

    @Test
    void testCreateShortUrl_Success() throws Exception {
        UrlDto requestDto = getValidUrlDto("https://request.com");
        String originalUrl = "https://response.com";
        UrlDto responseDto = getValidUrlDto(originalUrl);

        when(urlService.createShortUrl(requestDto)).thenReturn(responseDto);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.url").value(originalUrl));
    }

    @ParameterizedTest
    @MethodSource("getInvalidUrlDto")
    public void testCreateShortUrl_InvalidUrlDto(UrlDto urlDto) throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(urlDto)))
                .andExpect(status().isBadRequest());
    }

    public static Stream<Object[]> getInvalidUrlDto() {
        String validButTooLongUrl = "http://example.com?p=" + "too_long_url".repeat(180);

        return Stream.of(
                new Object[]{UrlDto.builder()
                        .url("")
                        .build()},
                new Object[]{UrlDto.builder()
                        .url("    ")
                        .build()},
                new Object[]{UrlDto.builder()
                        .url("no-valid-url")
                        .build()},
                new Object[]{UrlDto.builder()
                        .url(validButTooLongUrl)
                        .build()}
        );
    }

    public UrlDto getValidUrlDto(String url) {
        return UrlDto.builder()
                .url(url)
                .build();
    }
}