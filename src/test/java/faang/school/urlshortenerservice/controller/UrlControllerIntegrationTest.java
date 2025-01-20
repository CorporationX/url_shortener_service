package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.testcontainers.RedisContainer;
import faang.school.urlshortenerservice.config.json.JsonConfig;
import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.handler.ErrorResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class UrlControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = JsonConfig.configureObjectMapper();

    private static final String POST_URL = "/v1/url/shortener";
    private static final String GET_URL = "/v1/url/{hash}";

    @Container
    public static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.6");

    @Container
    public static final RedisContainer REDIS_CONTAINER
            = new RedisContainer(DockerImageName.parse("redis:latest"));

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        System.setProperty("spring.data.redis.host", REDIS_CONTAINER.getHost());
        System.setProperty("spring.data.redis.port", REDIS_CONTAINER.getMappedPort(6379).toString());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreateShortUrl() throws Exception {
        String url = "https://example.com";
        UrlRequestDto requestDto = new UrlRequestDto(url);

        String response = mockMvc.perform(post(POST_URL)
                        .header("x-user-id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        UrlResponseDto shortenedUrlDto = objectMapper.readValue(response, UrlResponseDto.class);
        Assertions.assertEquals(shortenedUrlDto.getUrl(), url);
    }

    @Test
    public void testCreateDuplicateURL() throws Exception {
        String url = "https://duplicate.com";
        UrlRequestDto requestDto = new UrlRequestDto(url);

        mockMvc.perform(post(POST_URL)
                        .header("x-user-id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();


        String response = mockMvc.perform(post(POST_URL)
                        .header("x-user-id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        ErrorResponse shortenedUrlDto = objectMapper.readValue(response, ErrorResponse.class);
        Assertions.assertEquals(shortenedUrlDto.getMessage(), "Url already exists: https://duplicate.com");
    }

    @Test
    public void testGetFUllURLByHash() throws Exception {
        String url = "https://google.com";
        UrlRequestDto requestDto = new UrlRequestDto(url);

        String response = mockMvc.perform(post(POST_URL)
                        .header("x-user-id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        UrlResponseDto shortenedUrlDto = objectMapper.readValue(response, UrlResponseDto.class);
        String hash = shortenedUrlDto.getHash();

        mockMvc.perform(get(GET_URL, hash)
                        .header("x-user-id", 1))
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, url));
    }

    @Test
    public void testGetFullURLHashDoesntExist() throws Exception {
        String hash = "nonExistingHash";

        String response = mockMvc.perform(get(GET_URL, hash)
                        .header("x-user-id", 1))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        ErrorResponse shortenedUrlDto = objectMapper.readValue(response, ErrorResponse.class);
        Assertions.assertEquals(shortenedUrlDto.getMessage(), "URL not found for hash: nonExistingHash");
    }

}

