package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.config.context.UserContext;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlController.class)
public class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlService urlService;

    @MockBean
    private UserContext userContext;

    @Autowired
    private ObjectMapper objectMapper;

    public static Stream<Arguments> invalidUrls() {
        return Stream.of(
                Arguments.of(new UrlDto("")),
                Arguments.of(new UrlDto("invalid-url")),
                Arguments.of(new UrlDto("http://")),
                Arguments.of(new UrlDto("http://123.1235.232.121")),
                Arguments.of(new UrlDto("xyz://example.com"))
        );
    }

    @Test
    void health_returnsOk() throws Exception {
        mockMvc.perform(get("/health")
                        .header("x-user-id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }

    @Test
    void shorten_returnsShortenedUrl() throws Exception {
        UrlDto dto = new UrlDto("http://example.com");
        UrlDto shortenedDto = new UrlDto("http://short.url/hash123");
        when(urlService.shortenUrl(dto)).thenReturn(shortenedDto);

        mockMvc.perform(post("/url")
                        .header("x-user-id", "1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"url\":\"http://short.url/hash123\"}"));
    }

    @ParameterizedTest
    @MethodSource("invalidUrls")
    void shorten_throwsExceptionForInvalidUrl(UrlDto urlDto) throws Exception {
        mockMvc.perform(post("/url")
                        .header("x-user-id", "1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(urlDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void redirect_returnsRedirectView() throws Exception {
        String hash = "hash123";
        String originalUrl = "http://example.com";
        when(urlService.getUrl(hash)).thenReturn(originalUrl);

        mockMvc.perform(get("/" + hash)
                        .header("x-user-id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(originalUrl));
    }

    @Test
    void redirect_throwsExceptionForInvalidHash() throws Exception {
        mockMvc.perform(get("/")
                        .header("x-user-id", "1"))
                .andExpect(status().isNotFound());
    }
}