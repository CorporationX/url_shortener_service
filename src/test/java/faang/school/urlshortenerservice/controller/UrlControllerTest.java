package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.config.context.UserContext;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

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
                        .content("{\"url\":\"http://example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"url\":\"http://short.url/hash123\"}"));
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