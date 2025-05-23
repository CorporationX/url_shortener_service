package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.config.context.UserContext;
import faang.school.urlshortenerservice.dto.ShortenUrlRequest;
import faang.school.urlshortenerservice.dto.ShortenedUrlResponse;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UrlController.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlService urlService;

    @MockBean
    private UserContext userContext;


    @Nested
    @DisplayName("POST /url - Создание короткой ссылки")
    class ShortenUrlEndpointTests {
        private final String validUrl = "https://example.com/very-long-url";
        private final String shortUrl = "http://localhost:8080/abc123";

        @Test
        @DisplayName("Когда валидный запрос, тогда возвращает короткий URL")
        void givenValidRequest_whenShortenUrl_thenReturnsShortUrl() throws Exception {
            when(urlService.shortenUrl(any(ShortenUrlRequest.class)))
                    .thenReturn(new ShortenedUrlResponse(shortUrl));

            mockMvc.perform(post("/url")
                            .header("x-user-id", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "originalUrl": "%s"
                                    }
                                    """.formatted(validUrl)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.url").value(shortUrl));
        }

        @Test
        @DisplayName("Когда URL невалидный, тогда возвращает 400")
        void givenInvalidUrl_whenShortenUrl_thenReturnsBadRequest() throws Exception {
            mockMvc.perform(post("/url")
                            .header("x-user-id", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "originalUrl": "not-a-url"
                                    }
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));
        }
    }

    @Nested
    @DisplayName("GET /url/{hash} - Редирект по хешу")
    class RedirectByHashEndpointTests {
        private final String hash = "abc123";
        private final String originalUrl = "https://example.com/original";

        @Test
        @DisplayName("Когда хеш существует, тогда возвращает 302 редирект")
        void givenExistingHash_whenRedirectByHash_thenReturnsRedirect() throws Exception {
            when(urlService.getOriginalUrl(hash))
                    .thenReturn(originalUrl);

            mockMvc.perform(get("/url/{hash}", hash)
                            .header("x-user-id", 1))
                    .andExpect(status().isFound());

            verify(urlService).getOriginalUrl(hash);
        }

        @Test
        @DisplayName("Когда хеш не существует, тогда возвращает 404")
        void givenNonExistingHash_whenRedirectByHash_thenReturnsNotFound() throws Exception {
            when(urlService.getOriginalUrl(hash))
                    .thenThrow(new UrlNotFoundException("URL not found"));

            mockMvc.perform(get("/url/{hash}", hash)
                            .header("x-user-id", 1))
                    .andExpect(status().isNotFound());
        }
    }
}
