package faang.school.urlshortenerservice.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.integration.IntegrationTestBase;
import faang.school.urlshortenerservice.model.dto.UrlDto;
import faang.school.urlshortenerservice.model.dto.UrlResponse;
import faang.school.urlshortenerservice.model.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class UrlControllerIT extends IntegrationTestBase {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private UrlRepository urlRepository;

    @BeforeEach
    public void init() {
        Objects.requireNonNull(cacheManager.getCache("urls")).clear();
    }

    @Test
    @DisplayName("Create short url creates url successfully")
    void testCreateShortUrl() throws Exception {
        var expectedEntity = Url.builder()
                .hash("1")
                .url("https://testurl.com/test/3")
                .build();

        var urlDto = new UrlDto("https://testurl.com/test/3", null);
        var body = objectMapper.writeValueAsString(urlDto);
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(body).
                        header("x-user-id", 1))
                .andExpectAll(status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON_VALUE),
                        jsonPath("$.url").value("https://testurl.com/1"))
                .andReturn();

        var response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UrlResponse.class).url();
        var hash = response.substring(response.lastIndexOf("/") + 1);
        var resultEntity = urlRepository.findById(hash);
        var url = Objects.requireNonNull(cacheManager.getCache("urls")).get(hash, String.class);

        assertThat(url)
                .withFailMessage("Value for key '%s' not found in cache", hash)
                .isNotBlank()
                .isEqualTo(urlDto.getUrl());
        assertThat(resultEntity).isPresent();
        assertThat(resultEntity.get())
                .usingRecursiveComparison()
                .ignoringFields("createdAt")
                .isEqualTo(expectedEntity);
    }

    @Test
    @DisplayName("Create short url throws MethodArgumentNotValidException")
    void testCreateShortUrlException() throws Exception {
        var urlDto = new UrlDto("ssps://testurl.com/test/3", null);
        String body = objectMapper.writeValueAsString(urlDto);
        mockMvc.perform(post("/api/v1/urls")
                        .header("x-user-id", 1)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpectAll(status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON_VALUE),
                        jsonPath("$.url").value("Invalid URL format!"));

        urlDto.setUrl("");
        body = objectMapper.writeValueAsString(urlDto);
        mockMvc.perform(post("/api/v1/urls")
                        .header("x-user-id", 1)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpectAll(status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON_VALUE),
                        jsonPath("$.url").value("URL can't be empty or null!"));
    }

    @Test
    @DisplayName("Create short URL throws EntityExistsException")
    void testCreateShortUrlExceptionEntityExists() throws Exception {
        var urlDto = new UrlDto("https://testurl.com/test/1", null);
        String body = objectMapper.writeValueAsString(urlDto);
        mockMvc.perform(post("/api/v1/urls")
                        .header("x-user-id", 1)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpectAll(status().isConflict(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.code").value(409),
                        jsonPath("$.message").value("URL %s already exists".formatted(urlDto.getUrl())));
    }

    @Test
    @DisplayName("Get original URL retrieve original URL successfully and puts it in cache")
    void testGetOriginalUrl() throws Exception {
        String hash = "3hB3us";
        String expectedUrl = "https://testurl.com/test/1";
        mockMvc.perform(get("/api/v1/urls/{hash}", hash)
                        .header("x-user-id", 1))
                .andExpectAll(status().isFound(),
                        header().string("Location", expectedUrl));

        String urlFromCache = Objects.requireNonNull(cacheManager.getCache("urls")).get(hash, String.class);
        assertThat(urlFromCache).isEqualTo(expectedUrl);
    }

    @Test
    @DisplayName("Get original URL throws EntityNotFoundException")
    void testGetOriginalUrlEntityNotFound() throws Exception {
        String hash = "3hB3ax";
        mockMvc.perform(get("/api/v1/urls/{hash}", hash)
                        .header("x-user-id", 1))
                .andExpectAll(status().isNotFound(),
                        jsonPath("$.code").value(404),
                        jsonPath("$.message").value("URL with hash %s not found".formatted(hash)));
    }
}