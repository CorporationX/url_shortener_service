package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.dto.ShortUrlDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private ObjectMapper objectMapper; // for mapping json

    @Autowired
    private DataSource dataSource;  // for executing sql query

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

    // 1. Тест на создание короткой ссылки
    @Test
    @DisplayName("Test create short url")
    public void testCreateShortUrl() throws Exception {
        String userId = "12345";
        String originalUrl = "https://www.example.com";

        // Выполняем запрос на создание короткой ссылки
        String shortUrlResponse = mockMvc.perform(post("/short-url")
                        .header("x-user-id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"url\": \"" + originalUrl + "\" }"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortUrl").exists())
                .andExpect(jsonPath("$.id").exists())  // Проверяем, что id присутствует
                .andReturn()
                .getResponse()
                .getContentAsString();

        ShortUrlDto shortUrlDto = parseShortUrlDto(shortUrlResponse);
        long id = shortUrlDto.getId();  // Получаем id

        // Проверяем наличие записи в базе данных напрямую через SQL по id
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM url WHERE id = ?")) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next(), "URL must be in db");
            assertEquals(originalUrl, rs.getString("url"), "URL must match with original");
        }
    }

    @Test
    @DisplayName("Test for redirection to stored original url by short url")
    public void testRedirectToOriginalUrl() throws Exception {
        String userId = "12345";
        String originalUrl = "https://www.example.com";
        String shortUrl = "bbbbbb";

        // Вставляем данные напрямую в БД через SQL и получаем id
        long id = insertUrlIntoDatabase(shortUrl, originalUrl);

        // Выполняем запрос на редирект по короткой ссылке
        mockMvc.perform(get("/short-url/" + shortUrl)
                        .header("x-user-id", userId))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", originalUrl));
    }

    @Test
    @DisplayName("Test update stored url")
    public void testUpdateShortUrl() throws Exception {
        String userId = "12345";
        String originalUrl = "https://www.example.com";
        String updatedUrl = "https://www.updated-example.com";
        String shortUrl = "aaaaaaaa";

        // Вставляем данные напрямую в БД через SQL и получаем id
        long urlId = insertUrlIntoDatabase(shortUrl, originalUrl);

        // Выполняем запрос на обновление ссылки
        mockMvc.perform(put("/short-url")
                        .header("x-user-id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"id\": " + urlId + ", \"url\": \"" + updatedUrl + "\" }"))
                .andExpect(status().isNoContent());

        // Проверяем, что запись обновлена в БД напрямую через SQL по id
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT url FROM url WHERE id = ?")) {
            stmt.setLong(1, urlId);
            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next(), "URL must be in db");
            assertEquals(updatedUrl, rs.getString("url"), "Stored URL must match with new URL");
        }

        // Выполняем запрос на редирект по обновленной ссылке
        mockMvc.perform(get("/short-url/" + shortUrl)
                        .header("x-user-id", userId))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", updatedUrl));
    }

    private long insertUrlIntoDatabase(String shortUrl, String originalUrl) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "INSERT INTO url (url, short_url, created_at, updated_at) VALUES (?, ?, ?, ?) RETURNING id")) {
            LocalDateTime now = LocalDateTime.now();
            stmt.setString(1, originalUrl);
            stmt.setString(2, shortUrl);
            stmt.setObject(3, now);
            stmt.setObject(4, now);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            } else {
                throw new SQLException("Failed to insert data into table url");
            }
        }
    }

    @Test
    @DisplayName("Complex test: create and redirect by created short url")
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
    @DisplayName("Complex test: create, update and redirected to updated url by short url")
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

    @Test
    @DisplayName("Test attempt redirect by unknowed url")
    public void testUrlNotFoundException() throws Exception {

        String nonExistentShortUrl = "wrong";

        mockMvc.perform(get("/short-url/" + nonExistentShortUrl)
                        .header("x-user-id", "1")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("error"))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.instance").value("/short-url/" + nonExistentShortUrl))
                .andExpect(jsonPath("$.url").value(nonExistentShortUrl));
    }

    @Test
    @DisplayName("Test attempt update non existent url")
    public void testUrlNotExistException() throws Exception {

        String nonExistentId = "-1";

        String requestBody = """
                {
                    "id": -1,
                    "url": "https://new_example.com"
                }
                """;

        mockMvc.perform(put("/short-url")
                        .header("x-user-id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("error"))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.id").value(nonExistentId));
    }

    @Test
    @DisplayName("Test attempt create short ulr with invalid original url")
    public void testMethodArgumentNotValidException() throws Exception {
        String requestBody = """
                {
                    "url": "invalid_url"
                }
                """;

        mockMvc.perform(post("/short-url")
                        .header("x-user-id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['invalid fields'].url").value("Invalid URL format"));
    }

    private ShortUrlDto parseShortUrlDto(String responseContent) throws Exception {
        return objectMapper.readValue(responseContent, ShortUrlDto.class);
    }
}

