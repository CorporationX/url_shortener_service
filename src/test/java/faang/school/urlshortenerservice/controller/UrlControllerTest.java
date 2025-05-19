package faang.school.urlshortenerservice.controller;

import com.redis.testcontainers.RedisContainer;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("UrlController Test")
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UrlService urlService;

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13.3");

    @Container
    private static final RedisContainer redis = new RedisContainer(RedisContainer.DEFAULT_IMAGE_NAME.withTag("7.0"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.generate-ddl", () -> true);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

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

        var result = mockMvc.perform(
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