package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.model.dto.UrlDto;
import faang.school.urlshortenerservice.util.PostgreSQLTestContainer;
import faang.school.urlshortenerservice.util.RedisTestContainer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UrlControllerIntgTest {

    @Autowired
    MockMvc mockMvc;

    private static PostgreSQLContainer<?> postgresContainer;
    private static GenericContainer<?> redisContainer;

    @BeforeAll
    public static void setup() {
        postgresContainer = PostgreSQLTestContainer.getPostgresContainer();
        redisContainer = RedisTestContainer.getRedisContainer();
    }

    @DynamicPropertySource
    static void overrideSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", postgresContainer::getDriverClassName);
        registry.add("spring.liquibase.enabled", () -> false);
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
    }

    @Order(1)
    @Test
    void testCreateShortUrlSuccess() throws Exception {
        UrlDto urlDto = new UrlDto();
        urlDto.setOriginalUrl("http://www.google.com");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(urlDto);

        mockMvc.perform(post("/api/v1/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string(matchesPattern("^http://denart\\.info/[a-zA-Z0-9]{1,6}$")))
                .andReturn();
    }

    @Order(2)
    @Test
    void testGetOriginalUrlSuccess() throws Exception {
        String hash = "455ded";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/url/{hash}", hash))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://fatum.ru"))
                .andReturn();
    }

    @Order(3)
    @Test
    void testGetOriginalUrlSuccess_whenRedisDown() throws Exception {
        String hash = "455ded";
        redisContainer.stop();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/url/{hash}", hash))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://fatum.ru"))
                .andReturn();
    }

    @Order(4)
    @Test
    void testCreateShortUrlSuccess_whenRedisDown() throws Exception {
        UrlDto urlDto = new UrlDto();
        urlDto.setOriginalUrl("http://www.google.com");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(urlDto);

        mockMvc.perform(post("/api/v1/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string(matchesPattern("^http://denart\\.info/[a-zA-Z0-9]{1,6}$")))
                .andReturn();

        redisContainer.start();
    }

}
