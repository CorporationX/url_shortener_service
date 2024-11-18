package faang.school.urlshortenerservice.contoller;

import com.redis.testcontainers.RedisContainer;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
@ActiveProfiles("test")
@Sql(scripts = "/sql/dataScript.sql")
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UrlCacheRepository urlCacheRepository;


    @Container
    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:latest");
    @Container
    private static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis/redis-stack:latest"));


    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);

    }

    @Test
    @DisplayName("Запуск контейнера")
    void testContainerIsRunning() {
        assertThat(POSTGRESQL_CONTAINER.isRunning()).as("PostgreSQL контейнер не запущен!").isTrue();
    }

    @Test
    void createShortUrlSuccess() throws Exception {
        String requestBody = """
                {
                "url": "http://sds"
                }
                """;
        MvcResult result = mockMvc.perform(post("/api/v1/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("x-user-id", 1))
                .andExpectAll(status().isCreated(),
                        content().string(startsWith("https://sh.com/")))
                .andReturn();

        String hash = result.getResponse().getContentAsString()
                .substring(result.getResponse().getContentAsString().lastIndexOf("/") + 1);
        assertNotNull(urlCacheRepository.getUrl(hash));
    }

    @Test
    void createShortUrlReturnBadRequest() throws Exception {
        String requestBody = """
                {}
                """;
        mockMvc.perform(post("/api/v1/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("x-user-id", 1))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOriginalUrlSuccess() throws Exception {
        String requestBody = """
                {
                    "url": "http://sh.com/i4W"
                }
                """;
        mockMvc.perform(get("/api/v1/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("x-user-id", 1))
                .andExpectAll(status().isFound(), header().string("Location", "http://test.com/test"));
    }

    @Test
    void getOriginalUrlReturnBadRequest() throws Exception {
        String requestBody = """
                {
                "url": "ht:test"
                }
                """;
        mockMvc.perform(get("/api/v1/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("x-user-id", 1))
                .andExpect(status().isBadRequest());
    }
}