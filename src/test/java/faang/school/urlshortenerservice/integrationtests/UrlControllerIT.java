package faang.school.urlshortenerservice.integrationtests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.testcontainers.RedisContainer;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class UrlControllerIT {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.6");

    @Container
    private static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis/redis-stack:latest"));

    @DynamicPropertySource
    static void start(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        registry.add("spring.data.redis.post", () -> REDIS_CONTAINER.getMappedPort(6379));
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
    }

    private Url url = new Url().builder()
            .hash("2")
            .url("https://")
            .build();

    private String endpointGetUrl = "/api/v1/url/get/{hash}";
    private String endpointCreateShortLink = "/api/v1/url/shorten";

    @BeforeEach
    void setUp() {
        urlRepository.save(url);
    }
    @Test
    void testGetUrlSuccessfulRedirect() throws Exception {
        mockMvc.perform(
                get(endpointGetUrl, 2)
                        .header("x-user-id", "1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().is3xxRedirection());
    }

    @Test
    void testGetUrlDontUrlInDatabase() throws Exception {
        mockMvc.perform(
                get(endpointGetUrl, 1)
                        .header("x-user-id", "1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    void testCreateShortLinkInvalidUrl() throws Exception {
        mockMvc.perform(
                post(endpointCreateShortLink)
                        .header("x-user-id", "1")
                        .content(objectMapper.writeValueAsString(UrlDto.builder().url("not url").build()))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void testCreateShortLinkEmptyUrl() throws Exception {
        mockMvc.perform(
                post(endpointCreateShortLink)
                        .header("x-user-id", "1")
                        .content(objectMapper.writeValueAsString(UrlDto.builder().url("").build()))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.url").value("URL must not be blank"));
    }

}
