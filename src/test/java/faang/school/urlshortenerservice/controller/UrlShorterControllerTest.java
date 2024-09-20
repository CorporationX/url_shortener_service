package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.dto.ShortUrlDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ContextConfiguration(initializers = UrlShorterControllerTest.Initializer.class)
public class UrlShorterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(
            "postgres:15.2"
    )
            .withDatabaseName("testdb")
            .withUsername("user")
            .withPassword("password");

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @BeforeAll
    public static void beforeAll() {
        postgreSQLContainer.start();
    }

    @AfterAll
    public static void afterAll() {
        postgreSQLContainer.stop();
    }

    @Test
    public void createShortUrlAndRedirect() throws Exception {
        String userId = "12345";
        String originalUrl = "https://www.example.com";

        // 1. Создание короткой ссылки
        String shortUrlResponse = mockMvc.perform(post("/short-url")
                        .header("x-user-id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"url\": \"" + originalUrl + "\" }"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortUrl").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Извлечение короткой ссылки из JSON-ответа
        ShortUrlDto shortUrlDto = parseShortUrlDto(shortUrlResponse);
        String shortUrl = shortUrlDto.getShortUrl();

        // 2. Переход по короткой ссылке
        mockMvc.perform(get("/short-url/" + shortUrl)
                        .header("x-user-id", userId))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", originalUrl));
    }

    @Test
    public void createUpdateAndRedirectShortUrl() throws Exception {
        String userId = "12345";
        String originalUrl = "https://www.example.com";
        String updatedUrl = "https://www.updated-example.com";

        // 1. Создание короткой ссылки
        String shortUrlResponse = mockMvc.perform(post("/short-url")
                        .header("x-user-id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"url\": \"" + originalUrl + "\" }")
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortUrl").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Извлечение короткой ссылки и ID
        ShortUrlDto shortUrlDto = parseShortUrlDto(shortUrlResponse);
        String shortUrl = shortUrlDto.getShortUrl();
        long id = shortUrlDto.getId();

        // 2. Обновление ссылки
        mockMvc.perform(put("/short-url")
                        .header("x-user-id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"id\": " + id + ", \"url\": \"" + updatedUrl + "\" }"))
                .andExpect(status().isNoContent());

        // 3. Переход по обновленной короткой ссылке
        mockMvc.perform(get("/short-url/" + shortUrl)
                        .header("x-user-id", userId))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", updatedUrl));
    }

    private ShortUrlDto parseShortUrlDto(String responseContent) throws Exception {
        return objectMapper.readValue(responseContent, ShortUrlDto.class);
    }
}

