package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UrlControllerTest {
    @InjectMocks
    private UrlController urlController;
    @Mock
    private UrlService urlService;
    private MockMvc mockMvc;

    private UrlDto urlDto;
    private String urlDtoJson;
    private String hash;
    private String redirectUrl;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        String url = "url";
        hash = "hash";
        redirectUrl = "redirectUrl";
        mockMvc = MockMvcBuilders.standaloneSetup(urlController).build();
        urlDto = UrlDto.builder()
                .url(url)
                .build();


        ObjectMapper objectMapper = new ObjectMapper();
        urlDtoJson = objectMapper.writeValueAsString(urlDto);
    }


    @Test
    @DisplayName("Testing creationShortUrl methods")
    void testCreateShortUrl() throws Exception {
        mockMvc.perform(post("/api/v1/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(urlDtoJson))
                .andExpect(status().isOk());

        verify(urlService, times(1)).createShortUrl(urlDto);
    }

    @Test
    @DisplayName("Testing redirect methods")
    void testRedirect() throws Exception {
        when(urlService.getUrlByHash(hash)).thenReturn(redirectUrl);

        mockMvc.perform(get("/api/v1/url/{hash}", hash))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", redirectUrl));

        verify(urlService, times(1)).getUrlByHash(hash);
    }
}