package faang.school.urlshortenerservice.controller;


import faang.school.urlshortenerservice.AbstractIntegrationTest;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static faang.school.urlshortenerservice.IntegrationTestConstants.EXISTING_HASH;
import static faang.school.urlshortenerservice.IntegrationTestConstants.CORRECT_URL;
import static faang.school.urlshortenerservice.IntegrationTestConstants.EMPTY_URL;
import static faang.school.urlshortenerservice.IntegrationTestConstants.INVALID_URL;
import static faang.school.urlshortenerservice.IntegrationTestConstants.NON_EXISTING_HASH;
import static faang.school.urlshortenerservice.IntegrationTestConstants.URL;
import static faang.school.urlshortenerservice.IntegrationTestConstants.USER_ID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UrlControllerIT extends AbstractIntegrationTest {

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private UrlCacheRepository urlCacheRepository;

    @Test
    void createShortLink_shouldReturnSuccessfullyResult() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/shortener/url")
                        .header("x-user-id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CORRECT_URL)))
                .andExpect(status().isOk())
                .andReturn();

        String hash = result.getResponse().getContentAsString();
        String savedHashInDB = urlRepository.getHash(CORRECT_URL.url());
        Optional<String> savedUrlInCache = urlCacheRepository.findUrlByHash(hash);

        assertThat(hash).isNotBlank();
        assertThat(savedHashInDB).isEqualTo(hash);
        assertThat(savedUrlInCache)
                .isPresent()
                .contains(CORRECT_URL.url());
    }

    @Test
    void createShortLink_shouldReturnBadRequest_whenUrlIsInvalid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/shortener/url")
                        .header("x-user-id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(INVALID_URL)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.url").value("Invalid URL"));
    }

    @Test
    void createShortLink_shouldReturnBadRequest_whenUrlIsEmpty() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/shortener/url")
                        .header("x-user-id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(EMPTY_URL)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.url")
                        .value("URL cannot be empty"));
    }

    @Test
    void getUrl_shouldRedirectToOriginalUrl_whenUrlFoundInCache() throws Exception {
        urlCacheRepository.save(CORRECT_URL.url(), EXISTING_HASH);

        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/api/v1/shortener/" + EXISTING_HASH))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(CORRECT_URL.url()));
    }

    @Test
    void getUrl_shouldRedirectToOriginalUrl_whenUrlFoundInDataBase() throws Exception {
        urlRepository.save(URL);

        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/api/v1/shortener/" + EXISTING_HASH))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(URL.getUrl()));
    }

    @Test
    void getUrl_shouldReturnNotFound_whenUrlNotFoundAnywhere() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/api/v1/shortener/" + NON_EXISTING_HASH))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(String.format("URL not found for hash: %s", NON_EXISTING_HASH)
                        ));
    }
}
