package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.testcontainers.RedisContainer;
import faang.school.urlshortenerservice.dto.UrlDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Testcontainers
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class UrlControllerItTest {

    @Container
    static PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("app_test")
                    .withUsername("test")
                    .withPassword("test");

    @Container
    static RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis/redis-stack:latest"));

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
    }

    @Value("${app.base-url}")
    private String baseUrl;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shortenUrl_whenValidDto_thenReturnShortenUrl() throws Exception {
        UrlDto dto = new UrlDto("https://example.com");

        mockMvc.perform(post("/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", "1")
                        .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isCreated())
                .andExpect(content().string(org.hamcrest.Matchers.startsWith(baseUrl + "/")));
    }

    @Test
    void shortenUrl_whenMalformedUrl_thenReturnsBadRequest() throws Exception {
        UrlDto dto = new UrlDto("ftp://example.com");

        mockMvc.perform(post("/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", "1")
                        .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void getLongUrl_whenAfterShortening_thenReturnsOriginalUrl() throws Exception {
        UrlDto dto = new UrlDto("https://example.com");
        String response = mockMvc.perform(post("/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", "1")
                        .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String hash = response.substring(response.lastIndexOf('/') + 1);

        mockMvc.perform(get("/" + hash)
                        .header("x-user-id", "1"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", dto.getUrl()));
    }

    @Test
    void getLongUrl_whenInvalidHashLength_thenReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/abc")
                        .header("x-user-id", "1"))
                .andExpect(status().isBadRequest());
    }
}