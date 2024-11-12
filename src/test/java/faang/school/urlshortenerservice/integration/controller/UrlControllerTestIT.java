package faang.school.urlshortenerservice.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.integration.IntegrationTestBase;
import faang.school.urlshortenerservice.model.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class UrlControllerTestIT extends IntegrationTestBase {
    private final UrlService urlService;
    private final MockMvc mockMvc;

    @Autowired
    public UrlControllerTestIT(UrlService urlService, MockMvc mockMvc) {
        this.urlService = urlService;
        this.mockMvc = mockMvc;
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
}
