package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.BaseContextIT;
import faang.school.urlshortenerservice.dto.UrlDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class UrlControllerIT extends BaseContextIT {

    @Value("${short-url.base-url}")
    private String baseUrl;

    @Value("${short-url.cache.ttl-minutes}")
    private int ttlMinutes;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    MockMvc mockMvc;

    @Test
    void createShortUrlNonValidUrlLength() throws Exception {
        String originalUrl = "https://%s.com".formatted("m".repeat(550));
        UrlDto urlDto = new UrlDto(originalUrl);

        performCreatingShortUrlExpectBadRequest(urlDto);
    }

    @Test
    void createShortUrlInvalidStructureTest() throws Exception {
        String firstOriginalUrl = "hp://youtube.com";
        String secondOriginalUrl = "youtube.com^^^";
        UrlDto firstUrlDto = new UrlDto(firstOriginalUrl);
        UrlDto secondUrlDto = new UrlDto(secondOriginalUrl);

        performCreatingShortUrlExpectBadRequest(firstUrlDto);
        performCreatingShortUrlExpectBadRequest(secondUrlDto);
    }

    @Test
    void createShortUrlNullUrlTest() throws Exception {
        UrlDto urlDto = new UrlDto();
        performCreatingShortUrlExpectBadRequest(urlDto);
    }

    @Test
    void createShortUrlTest() throws Exception {
        long requesterId = 11L;
        String originalUrl = "https://youtube.com";
        UrlDto urlDto = new UrlDto(originalUrl);
        String urlRequest = objectMapper.writeValueAsString(urlDto);

        MvcResult result = mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(urlRequest)
                        .header("x-user-id", requesterId))
                .andExpect(status().isCreated())
                .andReturn();

        String shortUrl = result.getResponse().getContentAsString();
        String hash = getHashFromUrl(shortUrl);

        assertTrue(existsByHash("url", hash));
        assertFalse(existsByHash("hash", hash));
    }

    @Test
    @Sql("/db/test_sql/insert_url_records.sql")
    void redirectValidTest() throws Exception {
        long requesterId = 11L;
        String originalUrl = "https://youtube.com";
        String hash = "hash1";
        String cacheName = "shortUrls";
        String redisKey = "%s::%s".formatted(cacheName, hash);

        MvcResult redirectUrlResponse = mockMvc.perform(get("%s/%s".formatted("/api/v1/urls", hash))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", requesterId))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String redirectedUrl = redirectUrlResponse.getResponse().getHeader("Location");

        assertTrue(existsByHash("url", hash));
        assertFalse(existsByHash("hash", hash));
        assertEquals(Boolean.TRUE, redisTemplate.hasKey(redisKey));
        assertTrue(Objects.requireNonNullElse(redisTemplate.getExpire(redisKey), 0L) <= ttlMinutes * 60L);
        assertEquals(originalUrl, redirectedUrl);
    }

    private void performCreatingShortUrlExpectBadRequest(UrlDto urlDto) throws Exception {
        long requesterId = 11L;
        String urlRequest = objectMapper.writeValueAsString(urlDto);

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(urlRequest)
                        .header("x-user-id", requesterId))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    private String getHashFromUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    private boolean existsByHash(String tableName, String hash) {
        String query = "SELECT count(*) FROM %s WHERE hash = ?".formatted(tableName);
        return Objects.requireNonNullElse(jdbcTemplate.queryForObject(query, Integer.class, hash), 0) == 1;
    }
}