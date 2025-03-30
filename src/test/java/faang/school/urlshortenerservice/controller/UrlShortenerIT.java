package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.testcontainers.RedisContainer;
import faang.school.urlshortenerservice.dto.UrlRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@TestPropertySource(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@SpringBootTest
class UrlShortenerIT {
    public static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER;
    public static final RedisContainer REDIS_CONTAINER;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    static {
        POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>("postgres:13.3")
                .withDatabaseName("postgres")
                .withUsername("user")
                .withPassword("password");

        REDIS_CONTAINER = new RedisContainer(DockerImageName.parse("redis:7.0.12"));

        POSTGRE_SQL_CONTAINER.start();
        REDIS_CONTAINER.start();
    }


    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRE_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRE_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRE_SQL_CONTAINER::getPassword);
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", REDIS_CONTAINER::getFirstMappedPort);
    }

    @Test
    void testCreateAndRedirectShortUrl() throws Exception {
        UrlRequest request = new UrlRequest("https://example.com", 5);

        String response = mockMvc.perform(post("/hash/url")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("/hash/")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String hash = response.substring(response.lastIndexOf("/") + 1);

        mockMvc.perform(get("/hash/" + hash))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com"));
    }

    @Test
    void shouldReturnBadRequest_whenTtlIsTooBig() throws Exception {
        UrlRequest request = new UrlRequest("https://example.com", 999999999);

        mockMvc.perform(post("/hash/url")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFound_whenHashIsUnknown() throws Exception {
        mockMvc.perform(get("/hash/unknownhash"))
                .andExpect(status().isNotFound());
    }
}