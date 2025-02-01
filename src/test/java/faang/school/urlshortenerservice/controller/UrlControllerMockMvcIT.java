package faang.school.urlshortenerservice.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.testcontainers.RedisContainer;
import faang.school.urlshortenerservice.dto.UrlCreateDto;
import faang.school.urlshortenerservice.model.Url;
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
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UrlControllerMockMvcIT {

  private static final String URL_POST = "/url";
  private static final String URL_GET = "/{hash}";
  private static final String VALID_URL = "https://google.com";
  private static final String VALID_URL_HASH = "d4q0";
  private static final String URL_IN_CACHE = "https://yahoo.com";
  private static final String HASH_IN_CACHE = "oooo";
  private static final String INVALID_URL = "not a link";
  private static final String HEADER_KEY = "x-user-id";

  private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @Autowired
  private UrlCacheRepository cacheRepository;

  @Autowired
  private MockMvc mockMvc;

  @Container
  public static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>(
      "postgres:13:6");

  @Container
  public static final RedisContainer REDIS_CONTAINER = new RedisContainer(DockerImageName.parse("redis/redis-stack:latest"));

  @DynamicPropertySource
  static void start(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
    registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
    registry.add("spring.liquibase.contexts", () -> "test");

    registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
    registry.add("spring.data.redis", REDIS_CONTAINER::getHost);

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  @DisplayName("Should return added url with hash when url is valid")
  void addValidUrl() throws Exception {
    UrlCreateDto dto = createDto(VALID_URL);

    mockMvc.perform(post(URL_POST)
        .header(HEADER_KEY, 1)
        .contentType(MediaType.APPLICATION_JSON)
        .content(OBJECT_MAPPER.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.url").value(VALID_URL))
        .andExpect(jsonPath("$.hash").exists());
  }

  @Test
  @DisplayName("Should return internal server error when url is not valid")
  void addInvalidUrl() throws Exception {
    UrlCreateDto dto = createDto(INVALID_URL);

    mockMvc.perform(post(URL_POST)
            .header(HEADER_KEY, 1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(OBJECT_MAPPER.writeValueAsString(dto)))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @DisplayName("Should redirect when url received from Postgres")
  void redirectByUrlFromDB() throws Exception {
    mockMvc.perform(get(URL_GET, VALID_URL_HASH)
        .header(HEADER_KEY, 1))
        .andExpect(status().is3xxRedirection());
  }

  @Test
  @DisplayName("Should redirect when url received from Redis")
  void redirectByCache() throws Exception {
    Url url = Url.builder()
        .url(URL_IN_CACHE)
        .hash(HASH_IN_CACHE)
        .build();
    cacheRepository.add(url);

    mockMvc.perform(get(URL_GET, HASH_IN_CACHE)
            .header(HEADER_KEY, 1))
        .andExpect(status().is3xxRedirection());
  }

  @Test
  @DisplayName("Should not redirect when url does not exist in Redis, Postgres")
  void redirectNotFound() throws Exception {
    mockMvc.perform(get(URL_GET, "test")
            .header(HEADER_KEY, 1))
        .andExpect(status().isInternalServerError());
  }

  private UrlCreateDto createDto(String url) {
    return UrlCreateDto.builder()
        .url(url)
        .build();
  }
}