package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.config.ContainersConfiguration;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ContainersConfiguration.class)
@AutoConfigureMockMvc
@DisplayName("UrlController Test")
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UrlService urlService;

    @Test
    @DisplayName("Redirect to original URL")
    void redirect() throws Exception {
        String originalUrl = "https://www.example.com";

        String hash = urlService.createShortUrl(new UrlDto(originalUrl));

        mockMvc.perform(
                        get(hash)
                                .header("x-user-id", "123"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(originalUrl));
    }

    @Test
    @DisplayName("Redirect to original URL with invalid hash")
    void redirectWithInvalidHash() throws Exception {
        String invalidHash = "invalid-hash";

        mockMvc.perform(
                        get("/" + invalidHash)
                                .header("x-user-id", "123"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Redirect to original URL with empty hash")
    void redirectWithNullHash() throws Exception {
        mockMvc.perform(
                        get("/")
                                .header("x-user-id", "123"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Create short URL")
    void createShortUrl() throws Exception {
        String originalUrl = "https://www.example.com";

        mockMvc.perform(
                        post("http://localhost:8080/url")
                                .contentType("application/json")
                                .header("x-user-id", "123")
                                .content("{\"url\": \"" + originalUrl + "\"}"))
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    @DisplayName("Create short URL with invalid URL")
    void createShortUrlWithInvalidUrl() throws Exception {
        String invalidUrl = "invalid-url";

        mockMvc.perform(
                        post("http://localhost:8080/url")
                                .contentType("application/json")
                                .header("x-user-id", "123")
                                .content("{\"url\": \"" + invalidUrl + "\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create short URL with empty URL")
    void createShortUrlWithNullUrl() throws Exception {
        mockMvc.perform(
                        post("http://localhost:8080/url")
                                .contentType("application/json")
                                .header("x-user-id", "123")
                                .content("{\"url\": null}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create short URL with empty body")
    void createShortUrlWithEmptyBody() throws Exception {
        mockMvc.perform(
                        post("http://localhost:8080/url")
                                .contentType("application/json")
                                .header("x-user-id", "123")
                                .content("{}"))
                .andExpect(status().isBadRequest());
    }
}