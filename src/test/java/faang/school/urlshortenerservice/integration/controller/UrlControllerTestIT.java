package faang.school.urlshortenerservice.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.integration.IntegrationTestBase;
import faang.school.urlshortenerservice.model.dto.UrlDto;
import faang.school.urlshortenerservice.repository.RedisCacheRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class UrlControllerTestIT extends IntegrationTestBase {
    private final MockMvc mockMvc;
    private final RedisCacheRepository redisCacheRepository;

    @Autowired
    public UrlControllerTestIT(MockMvc mockMvc, RedisCacheRepository redisCacheRepository) {
        this.mockMvc = mockMvc;
        this.redisCacheRepository = redisCacheRepository;
    }

    @Test
    void test_CreateShortUrl_BadUrl() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UrlDto badUrlDto = UrlDto.builder().build();

        mockMvc.perform(post("/api/v1/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badUrlDto))
                        .header("x-user-id", 1))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test_CreateShortUrl_Ok() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UrlDto badUrlDto = UrlDto.builder().url("http://someurl.ru/eqweQWD123").build();

        MvcResult result = mockMvc.perform(post("/api/v1/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badUrlDto))
                        .header("x-user-id", 1))
                .andExpectAll(status().isCreated(),
                        content().string(startsWith("http://localhost:8080/api/v1/url/")))
                .andReturn();

        String hash = result.getResponse().getContentAsString()
                .substring(result.getResponse().getContentAsString().lastIndexOf("/") + 1);
        assertNotNull(redisCacheRepository.get(hash));
    }

    @Test
    void testGetOriginalUrl_OK() throws Exception {
        mockMvc.perform(get("/api/v1/url/{hash}", "i4W")
                        .header("x-user-id", 1))
                .andExpectAll(status().isFound(),
                        header().string("Location", "http://bigurl.com/123qedadsr"));
    }
}
