package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.cache.HashInitializer;
import faang.school.urlshortenerservice.model.dto.UrlDto;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.ContainerCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
public class UrlControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    HashInitializer hashInitializer;

    @Autowired
    HashCache hashCache;

    @Autowired
    UrlRepository urlRepository;

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = ContainerCreator.POSTGRES_CONTAINER;

    private ObjectMapper objectMapper;
    private long userId;

    @DynamicPropertySource
    static void overrideSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", postgresContainer::getDriverClassName);
        registry.add("spring.liquibase.enabled", () -> false);
    }

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        userId = 108;
        hashInitializer.initializeHashTable();
    }

    @Test
    @DisplayName("Should redirect to original url")
    public void testRedirectToOriginalUrl_Success() throws Exception {
        String hashString = "9WH8As";
        String originalUrl = "https://example.com";

        mockMvc.perform(get("/api/v1/shortener-service/{hash}", hashString)
                        .header("x-user-id", userId))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(originalUrl))
                .andReturn();
    }

    @Test
    @DisplayName("Should return shortened URL from database ")
    public void testShrinkUrl_Existing() throws Exception {
        UrlDto urlDto = new UrlDto();
        urlDto.setUrl("https://www.lipsum.com");

        String expectedShortUrl = "https://urlshrinker.com/Uy30G1";

        mockMvc.perform(post("/api/v1/shortener-service")
                        .header("x-user-id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(urlDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(is(expectedShortUrl)));
    }

    @Test
    @DisplayName("Should return shortened URL and save it in database ")
    public void testShrinkUrl_SaveAndReturn() throws Exception {
        UrlDto urlDto = new UrlDto();
        urlDto.setUrl("https://www.random-site.com");

        String expectedShortUrl = "https://urlshrinker.com/T45uI9";

        mockMvc.perform(post("/api/v1/shortener-service")
                        .header("x-user-id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(urlDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(is(expectedShortUrl)));

        assertThat(urlRepository.count()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should return validation error in case of empty URL")
    public void testShrinkUrl_InvalidUrlDto() throws Exception {
        String invalidUrlDto = "{}";

        mockMvc.perform(post("/api/v1/shortener-service")
                        .header("x-user-id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidUrlDto))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("URL cannot be empty"));
    }

    @Test
    @DisplayName("Should return BadRequest and ErrorResponse when entity not found")
    public void testShrinkUrl_NonExistingHash() throws Exception {
        String nonExistingHash = "non existing hash";

        mockMvc.perform(get("/api/v1/shortener-service/{hash}", nonExistingHash)
                        .header("x-user-id", userId))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Entity Not Found"))
                .andExpect(jsonPath("$.message").value("The specified shortened URL does not exist." +
                        " Please create a new shortened URL."));
    }

    @Test
    @DisplayName("Should return validation error in case of incorrect URL")
    public void testShrinkUrl_IncorrectURL() throws Exception {
        UrlDto incorrectURL = new UrlDto();
        incorrectURL.setUrl("www.example.com");

        mockMvc.perform(post("/api/v1/shortener-service")
                        .header("x-user-id", 12345)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incorrectURL)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("Invalid URL format"));
    }
}
