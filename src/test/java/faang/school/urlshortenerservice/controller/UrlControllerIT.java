package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.testcontainers.RedisContainer;
import faang.school.urlshortenerservice.dto.error.UrlShortenerErrorResponseDto;
import faang.school.urlshortenerservice.dto.url.UrlRequestDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class UrlControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UrlRepository urlRepository;
    @Container
    public static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.3")
                    .withDatabaseName("test_db")
                    .withUsername("test")
                    .withPassword("test");

    @Container
    public static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis/redis-stack:latest"));


    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);

        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
    }

    @Test
    @DisplayName("Успешное генерация хэша с записью в БД и Redis")
    public void generateHash_successfully() throws Exception {
        long userId = 9L;
        String url = "https://com.faang.school/api/v1/users";

        UrlRequestDto requestDto = new UrlRequestDto(url);

        MvcResult mvcResult = mockMvc.perform(
                        post("/api/v1/url")
                                .header("x-user-id", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                ).andExpect(status().isCreated())
                .andReturn();

        // TODO: как получить ответ
        String responseDto = mvcResult.getResponse().getContentAsString();

        Url urlEntity = urlRepository.findById(responseDto)
                .orElseThrow(() -> new AssertionError("Url not found in DB"));

        assertThat(urlEntity)
                .extracting(Url::getHash,
                        Url::getUrl
                )
                .containsExactly(
                        responseDto,
                        url
                );
        assertThat(urlEntity.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Создание хэша с невалидным url")
    public void generateHash_urlNotValid() throws Exception {
        long userId = 9L;
        String url = "not_valid_url";

        long beforeCount = urlRepository.count();

        UrlRequestDto requestDto = new UrlRequestDto(url);

        MvcResult mvcResult = mockMvc.perform(
                        post("/api/v1/url")
                                .header("x-user-id", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                ).andExpect(status().isBadRequest())
                .andReturn();

        UrlShortenerErrorResponseDto responseDto = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                UrlShortenerErrorResponseDto.class
        );

        assertThat(responseDto.getCodeResponse()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        long afterCount = urlRepository.count();
        assertThat(afterCount).isEqualTo(beforeCount);
    }
}
