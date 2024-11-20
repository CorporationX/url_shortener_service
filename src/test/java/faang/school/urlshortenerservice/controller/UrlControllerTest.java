package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.config.properties.url.UrlProperties;
import faang.school.urlshortenerservice.dto.LongUrlDto;
import faang.school.urlshortenerservice.service.url.UrlService;
import faang.school.urlshortenerservice.util.BaseContextTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class UrlControllerTest extends BaseContextTest {

    @Autowired
    private UrlController urlController;

    @Autowired
    private UrlService urlService;

    @Autowired
    private UrlProperties urlProperties;

    private static final String LONG_URL = "https://faang-school.com/courses/4jnzmndg/69zry8gd";

    private LongUrlDto longUrlDto;
    private LongUrlDto notValidDto;
    private String urlContent;

    @BeforeAll
    void setUp() {
        longUrlDto = LongUrlDto.builder()
                .url(LONG_URL)
                .build();

        notValidDto = LongUrlDto.builder()
                .url("Some url")
                .build();
    }

    @Test
    @DisplayName("When endpoint with valid dto passed returns created status with short url link")
    public void whenValidDtoPassedThenReturnCreatedStatusAndShortUrl() throws Exception {
        urlContent = objectMapper.writeValueAsString(longUrlDto);
        MvcResult result = mockMvc.perform(post("/v1/urls")
                .header("x-user-id", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(urlContent)
        ).andExpect(status().isCreated()).andReturn();

        String responseFromController = result.getResponse().getContentAsString();
        int lastSlashIndex = responseFromController.lastIndexOf('/');
        String suffix = responseFromController.substring(lastSlashIndex + 1);
        assertEquals(urlProperties.getUrlShort().getBaseUrl() + suffix, responseFromController);
    }

    @Test
    @DisplayName("When method called with not valid dto then returns BAD REQUEST")
    public void whenNotValidDtoPassedThenReturnBadRequestStatus() throws Exception {
        urlContent = objectMapper.writeValueAsString(notValidDto);
        mockMvc.perform(post("/v1/urls")
                .header("x-user-id", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(urlContent)
        ).andExpect(status().isBadRequest());
    }
}
