package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.AbstractIntegrationTest;
import faang.school.urlshortenerservice.config.AppProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static faang.school.urlshortenerservice.TestUtils.EMPTY_URL_DTO;
import static faang.school.urlshortenerservice.TestUtils.INVALID_URL_DTO;
import static faang.school.urlshortenerservice.TestUtils.NON_EXISTENT_HASH;
import static faang.school.urlshortenerservice.TestUtils.USER_ID;

public class UrlControllerIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private AppProperties appProperties;

    @BeforeEach
    public void setUp() {
        testDatabaseCleaner.restartUniqueNumberSequence();
        testDatabaseCleaner.truncateHashTable();
        testDatabaseCleaner.truncateUrlTable();
        testDatabaseCleaner.flushRedisDatabase();
    }

    @Test
    public void createShortLink_shouldThrowBadRequestException_whenTheTransmittedUrlIsEmpty() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/shortener/url")
                        .header("x-user-id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(EMPTY_URL_DTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.url")
                        .value("URL cannot be empty"));
    }

    @Test
    public void createShortLink_shouldThrowBadRequestException_whenTheTransmittedUrlIsIncorrect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/shortener/url")
                        .header("x-user-id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(INVALID_URL_DTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.url")
                        .value("Invalid URL"));
    }

    @Test
    public void redirect_shouldThrowNotFoundException_whenTheTransmittedHashDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/shortener/{hash}", NON_EXISTENT_HASH)
                        .header("x-user-id", USER_ID))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(String.format("Url for short link %s%s not found",
                                appProperties.baseUrl(),
                                NON_EXISTENT_HASH)
                        ));
    }
}
