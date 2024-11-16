package faang.school.urlshortenerservice.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.integration.IntegrationTestBase;
import faang.school.urlshortenerservice.model.dto.UrlDto;
import faang.school.urlshortenerservice.model.dto.UrlResponse;
import faang.school.urlshortenerservice.model.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

        var response  = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UrlResponse.class).url();
        var hash = response.substring(response.lastIndexOf("/") + 1);
        var resultEntity = urlRepository.findById(hash);

        String url = Objects.requireNonNull(cacheManager.getCache("urls")).get(hash, String.class);
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
}