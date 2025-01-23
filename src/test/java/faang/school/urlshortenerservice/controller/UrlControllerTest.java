package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.redis.testcontainers.RedisContainer;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.model.url.Url;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class UrlControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Container
    public static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER
            = new PostgreSQLContainer<>("postgres:13.3");

    @Container
    public static final RedisContainer REDIS_CONTAINER
            = new RedisContainer(DockerImageName.parse("redis/redis-stack:latest"));

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public static void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) throws InterruptedException {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        Thread.sleep(1000);
    }

    @Test
    void testRedirectToOriginalFromCache() throws Exception {
        String hash = "hash1";
        String url = "http://smthng1.com";
        redisTemplate.opsForValue().set(hash, url);
        MvcResult result = mockMvc.perform(
                get("/api/v1/url/" + hash)
                        .header("x-user-id", 1)
        ).andExpect(status().is3xxRedirection()).andReturn();
        assertEquals(url, result.getResponse().getHeader("Location"));
    }

    @Test
    void testRedirectToOriginalFromDb() throws Exception {
        String hash = "hash2";
        String url = "http://smthng2.com";
        jdbcTemplate.update("insert into url (hash, url) values (?, ?)", hash, url);
        MvcResult result = mockMvc.perform(
                get("/api/v1/url/" + hash)
                        .header("x-user-id", 1)
        ).andExpect(status().is3xxRedirection()).andReturn();
        assertEquals(url, result.getResponse().getHeader("Location"));
    }

    @Test
    void testCreateShort() throws Exception {
        UrlDto urlDto = UrlDto.builder().url("http://smthng3.com").build();
        MvcResult mvcResult = mockMvc.perform(
                post("/api/v1/url")
                        .header("x-user-id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(urlDto))
        ).andExpect(status().isCreated()).andReturn();
        String urlFromRedis = redisTemplate
                .opsForValue()
                .get(mvcResult.getResponse().getContentAsString());
        assertEquals(urlDto.getUrl(), urlFromRedis);
        String urlFromDb
                = jdbcTemplate
                .queryForObject("select url from url where hash = ?",
                        String.class,
                        mvcResult.getResponse().getContentAsString());
        assertEquals(urlDto.getUrl(), urlFromDb);
    }
}