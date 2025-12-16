package faang.school.urlshortenerservice.controller;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.dto.CreateShortUrlDto;
import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.service.UrlService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class UrlControllerTest {
    private MockMvc mockMvc;

    @Mock
    private UrlService urlService;

    @InjectMocks
    private UrlController urlController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(urlController).build();
    }

    @Test
    void createShortUrl_ReturnsShortUrl() throws Exception {
        String anyCorrectUrl = "https://translate.yandex.ru/";
        CreateShortUrlDto anyCreateShortUrlDto = new CreateShortUrlDto(anyCorrectUrl);

        when(urlService.createShortUrl(any(CreateShortUrlDto.class))).thenReturn(new ShortUrlDto(anyCorrectUrl));

        mockMvc.perform(post("/api/v1/urls")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(anyCreateShortUrlDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortUrl", is(anyCorrectUrl)));
    }

    @Test
    void createShortUrl_InvalidUrlInRequest() throws Exception {
        String anyIncorrectUrl = "anyInvalidUrl";
        CreateShortUrlDto anyCreateShortUrlDto = new CreateShortUrlDto(anyIncorrectUrl);

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(anyCreateShortUrlDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void passOriginalUrl_SuccessfullyRedirects() throws Exception {
        String anyCorrectUrl = "https://translate.yandex.ru/";
        String anyExistentHash = "anyExistentHash";
        when(urlService.getOriginalUrl(anyExistentHash)).thenReturn(anyCorrectUrl);

        mockMvc.perform(get("/api/v1/urls/anyExistentHash"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(anyCorrectUrl));
    }
}

