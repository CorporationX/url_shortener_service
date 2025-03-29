package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.AbstractIntegrationTest;
import faang.school.urlshortenerservice.config.AppProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UniqueNumberRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static faang.school.urlshortenerservice.TestConstants.CORRECT_URL_DTO;
import static faang.school.urlshortenerservice.TestConstants.EMPTY_URL_DTO;
import static faang.school.urlshortenerservice.TestConstants.INVALID_URL_DTO;
import static faang.school.urlshortenerservice.TestConstants.NON_EXISTENT_HASH;
import static faang.school.urlshortenerservice.TestConstants.USER_ID;
import static faang.school.urlshortenerservice.TestConstants.getHashFromShortLink;

class UrlControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UniqueNumberRepository uniqueNumberRepository;

    @Autowired
    private HashRepository hashRepository;

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private UrlCacheRepository urlCacheRepository;

    @BeforeEach
    public void setUp() {
        resetDatabase();
        flushRedisDatabase();
    }

    @Test
    public void createShortLink_shouldBeCompletedSuccessfully() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/shortener/url")
                        .header("x-user-id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CORRECT_URL_DTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String shortLink = result.getResponse().getContentAsString();
        String hash = getHashFromShortLink(shortLink);

        Assertions.assertEquals(urlRepository.get(hash), CORRECT_URL_DTO.url());
        Assertions.assertEquals(urlCacheRepository.get(hash).orElseThrow(), CORRECT_URL_DTO.url());
    }

    @Test
    public void createShortLink_shouldThrowBadRequestException_whenTheTransmittedUrlIsEmpty() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/shortener/url")
                        .header("x-user-id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(EMPTY_URL_DTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.url")
                        .value("URL cannot be empty"));
    }

    @Test
    public void createShortLink_shouldThrowBadRequestException_whenTheTransmittedUrlIsIncorrect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/shortener/url")
                        .header("x-user-id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(INVALID_URL_DTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.url")
                        .value("Invalid URL"));
    }

    @Test
    public void getUrl_shouldBeCompletedSuccessfully() throws Exception {
        MvcResult createShortLinkResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/shortener/url")
                        .header("x-user-id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CORRECT_URL_DTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String shortLink = createShortLinkResult.getResponse().getContentAsString();
        String hash = getHashFromShortLink(shortLink);

        MvcResult getUrlResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/shortener/{hash}", hash)
                        .header("x-user-id", USER_ID))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andReturn();

        String url = getUrlResult.getResponse().getHeader("Location");

        Assertions.assertEquals(urlRepository.get(hash), url);
        Assertions.assertEquals(urlCacheRepository.get(hash).orElseThrow(), url);
    }

    @Test
    public void getUrl_shouldThrowNotFoundException_whenTheTransmittedHashDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/shortener/{hash}", NON_EXISTENT_HASH)
                        .header("x-user-id", USER_ID))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(String.format("Url for short link %s%s not found",
                                appProperties.baseUrl(),
                                NON_EXISTENT_HASH)
                        ));
    }

    private void resetDatabase() {
        uniqueNumberRepository.resetSequence();
        hashRepository.truncateTable();
        urlRepository.truncateTable();
    }

    private void flushRedisDatabase() {
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.serverCommands().flushDb();
            return null;
        });
    }
}
