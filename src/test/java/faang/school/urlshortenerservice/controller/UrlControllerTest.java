package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.config.context.UserContext;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlController.class)
public class UrlControllerTest {
    private static final String URL = "https://example.com";
    private static final String SHORT_URL = "http://short.ly/abc123";
    private static final String HASH = "abc123";
    private static final String BAD_HASH = "invalid";
    private static final String USER_HEADER = "x-user-id";
    private static final String USER_ID = "11";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlService urlService;
    @MockBean
    private UserContext userContext;

    @Test
    @DisplayName("Создание короткой ссылки - успешно")
    void createShortUrlSuccess() throws Exception {
        when(urlService.createShortUrl(URL)).thenReturn(new UrlResponseDto(URL, SHORT_URL));

        mockMvc.perform(post("/url")
                        .header(USER_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"originalUrl\":\"" + URL + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.originalUrl").value(URL))
                .andExpect(jsonPath("$.shortUrl").value(SHORT_URL));
    }

    @Test
    @DisplayName("Редирект по валидному хэшу - успешно")
    void redirectValidHash() throws Exception {
        when(urlService.getOriginalUrl(HASH)).thenReturn(URL);

        mockMvc.perform(get("/url/" + HASH)
                        .header(USER_HEADER, USER_ID))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", URL));
    }

    @Test
    @DisplayName("Редирект по несуществующему хэшу - ошибка")
    void redirectInvalidHash() throws Exception {
        when(urlService.getOriginalUrl(BAD_HASH))
                .thenThrow(new UrlNotFoundException("URL not found"));

        mockMvc.perform(get("/url/" + BAD_HASH)
                        .header(USER_HEADER, USER_ID))
                .andExpect(status().isNotFound());
    }

}
