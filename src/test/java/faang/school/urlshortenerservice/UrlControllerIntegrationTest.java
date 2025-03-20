package faang.school.urlshortenerservice.api;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
class UrlControllerIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private HashRepository hashRepository;

    @Autowired
    private UrlService urlService;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("app.base-url", () -> "http://short.url/");
        registry.add("redis.ttl-hours", () -> "1");
        registry.add("spring.redis.host", () -> "localhost");
        registry.add("spring.redis.port", () -> "6379");
    }

    @BeforeEach
    void setUp() {
        hashRepository.saveAll(List.of(new Hash("abc123"), new Hash("xyz789")));
    }

    @Test
    void testCreateShortUrl_Success() throws Exception {
        UrlDto urlDto = new UrlDto();
        urlDto.setUrl("https://example.com");

        mockMvc.perform(post("/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\": \"https://example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string(startsWith("http://short.url/")))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().length() > 15));

        List<Url> urls = urlRepository.findAll();
        assertEquals(1, urls.size());
        assertEquals("https://example.com", urls.get(0).getUrl());
    }

    @Test
    void testCreateShortUrl_InvalidUrl() throws Exception {
        mockMvc.perform(post("/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\": \"invalid-url\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Invalid URL format")));
    }

    @Test
    void testRedirect_Success() throws Exception {
        urlRepository.save(new Url("abc123", "https://example.com", null));

        mockMvc.perform(get("/url/abc123"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com"));
    }

    @Test
    void testRedirect_UrlNotFound() throws Exception {
        mockMvc.perform(get("/url/nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("URL not found")));
    }

    @Test
    void testFullCycle_CreateAndRedirect() throws Exception {
        String response = mockMvc.perform(post("/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\": \"https://test.com\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String shortUrl = response.replace("http://short.url/", "");

        mockMvc.perform(get("/url/" + shortUrl))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://test.com"));
    }
}