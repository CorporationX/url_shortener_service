package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.BaseContextIT;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.properties.short_url.UrlCacheProperties;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class UrlControllerIT extends BaseContextIT {

    @Value("${short-url.base-path}")
    private String endpointBasePath;

    @Autowired
    private UrlCacheProperties urlCacheProperties;

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
        long requesterId = 11L;
        String firstOriginalUrl = "y.c";
        String secondOriginalUrl = "%s.com".formatted("".repeat(550));
        UrlDto firstUrlDto = new UrlDto(firstOriginalUrl);
        UrlDto secondUrlDto = new UrlDto(secondOriginalUrl);

        performCreatingShortUrlExpectBadRequest(firstUrlDto, requesterId);
        performCreatingShortUrlExpectBadRequest(secondUrlDto, requesterId);
    }

    @Test
    void createShortUrlInvalidStructureTest() throws Exception {
        long requesterId = 11L;
        String firstOriginalUrl = "hp://youtube.com";
        String secondOriginalUrl = "youtube.com^^^";
        UrlDto firstUrlDto = new UrlDto(firstOriginalUrl);
        UrlDto secondUrlDto = new UrlDto(secondOriginalUrl);

        performCreatingShortUrlExpectBadRequest(firstUrlDto, requesterId);
        performCreatingShortUrlExpectBadRequest(secondUrlDto, requesterId);
    }

    @Test
    void createShortUrlTest() throws Exception {
        long requesterId = 11L;
        String originalUrl = "youtube.com";
        UrlDto urlDto = new UrlDto(originalUrl);
        String urlRequest = objectMapper.writeValueAsString(urlDto);

        MvcResult result = mockMvc.perform(post(endpointBasePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(urlRequest)
                        .header("x-user-id", requesterId))
                .andExpect(status().isCreated())
                .andReturn();

        String shortUrl = result.getResponse().getContentAsString();
        String hash = getHashFromUrl(shortUrl);
        String redisKey = "%s::%s".formatted(urlCacheProperties.getDefaultCacheName(), hash);
        System.out.println("REDIS KEY:" + redisKey);

        assertTrue(existsByHash("url", hash));
        assertFalse(existsByHash("hash", hash));
        assertEquals(Boolean.TRUE, redisTemplate.hasKey(redisKey));
        assertTrue(Objects.requireNonNullElse(redisTemplate.getExpire(redisKey), 0L)
                <= urlCacheProperties.getDefaultTtlMinutes() * 60L);
    }

    @Test
    @Sql("/db/test_sql/insert_url_records.sql")
    void redirectValidTest() throws Exception {
        long requesterId = 11L;
        String originalUrl = "youtube.com";
        String hash = "hash1";
        String redisKey = "%s::%s".formatted(urlCacheProperties.getDefaultCacheName(), hash);

        MvcResult redirectUrlResponse = mockMvc.perform(get("%s/%s".formatted(endpointBasePath, hash))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", requesterId))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String redirectedUrl = redirectUrlResponse.getResponse().getHeader("Location");

        assertTrue(existsByHash("url", hash));
        assertFalse(existsByHash("hash", hash));
        assertNotNull(redisTemplate.opsForZSet().score(urlCacheProperties.getPopularCacheName(), hash));
        assertEquals(Boolean.TRUE, redisTemplate.hasKey(redisKey));
        assertTrue(Objects.requireNonNullElse(redisTemplate.getExpire(redisKey), 0L)
                <= urlCacheProperties.getDefaultTtlMinutes() * 60L);
        assertEquals("http://%s".formatted(originalUrl), redirectedUrl);
    }

    private void performCreatingShortUrlExpectBadRequest(UrlDto urlDto, long requesterId) throws Exception {
        String urlRequest = objectMapper.writeValueAsString(urlDto);

        mockMvc.perform(post(endpointBasePath)
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
