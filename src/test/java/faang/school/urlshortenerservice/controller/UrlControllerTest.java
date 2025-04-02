package faang.school.urlshortenerservice.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.HashService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;


@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
public class UrlControllerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Container
    static GenericContainer<?> redis =
        new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @MockBean
    private HashService hashService;

    @BeforeEach
    void setUp() {
        when(hashService.getFreeHash()).thenReturn("mockedHash123");
        urlRepository.deleteAll();
    }

    @Test
    void testCreateShortUrl_ShouldReturnCreated() throws Exception {
        UrlRequestDto urlRequestDto = new UrlRequestDto();
        urlRequestDto.setLongUrl("https://example.com");
        String url = objectMapper.writeValueAsString(urlRequestDto.getLongUrl());

        mockMvc.perform(post("/url")
                .contentType(MediaType.APPLICATION_JSON)
                .content(url))
            .andExpect(content().string("http://localhost:8080/mockedHash123"));
    }

    @Test
    void testCreateShortUrl_ShouldReturnExistingShortUrl_IfUrlAlreadyExists() throws Exception {
        UrlRequestDto urlRequestDto = new UrlRequestDto();
        urlRequestDto.setLongUrl("https://example.com");
        String url = objectMapper.writeValueAsString(urlRequestDto.getLongUrl());

        String hash = "abc123";
        urlRepository.save(Url.builder()
            .hash(hash)
            .url(url)
            .expiredAt(LocalDateTime.now().plusDays(1))
            .build());

        mockMvc.perform(post("/url")
                .contentType(MediaType.APPLICATION_JSON)
                .content(url))
            .andExpect(content().string("http://localhost:8080/mockedHash123"));
    }

    @Test
    void testRedirectToOriginalUrl_ShouldRedirectToOriginalUrl() throws Exception {
        String hash = "abc123";
        String originalUrl = "https://example.com";
        urlRepository.save(Url.builder()
            .hash(hash)
            .url(originalUrl)
            .expiredAt(LocalDateTime.now().plusDays(1))
            .build());

        mockMvc.perform(get("/url/" + hash))
            .andExpect(status().isFound())
            .andExpect(header().string("Location", originalUrl));
    }

    @Test
    void testRedirectToOriginalUrl_ShouldUseCache_IfUrlIsCached() throws Exception {
        String hash = "abc123";
        String originalUrl = "https://example.com";
        urlRepository.save(Url.builder()
            .hash(hash)
            .url(originalUrl)
            .expiredAt(LocalDateTime.now().plusDays(1))
            .build());

        mockMvc.perform(get("/url/" + hash));

        urlRepository.deleteAll();

        mockMvc.perform(get("/url/" + hash))
            .andExpect(status().isFound())
            .andExpect(header().string("Location", originalUrl));
    }
}