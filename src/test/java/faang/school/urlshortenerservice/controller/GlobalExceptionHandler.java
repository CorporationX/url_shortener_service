package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.config.context.UserContext;
import faang.school.urlshortenerservice.exception.HashUnavailableException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GlobalExceptionHandler.class)
@DisplayName("Тесты GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlController urlController;

    @MockBean
    private UserContext userContext;

    @Nested
    @DisplayName("Обработка HashUnavailableException")
    class HashUnavailableExceptionHandling {

        @Test
        @DisplayName("Когда сервис недоступен, тогда возвращает 503 с описанием ошибки")
        void whenHashServiceUnavailable_thenReturnsServiceUnavailable() throws Exception {
            when(urlController.shortenUrl(any()))
                    .thenThrow(new HashUnavailableException("Service temporarily down"));

            mockMvc.perform(post("/url")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("x-user-id", 1)
                            .content("{\"originalUrl\":\"https://example.com\"}"))
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(jsonPath("$.code").value("HASH_UNAVAILABLE"))
                    .andExpect(jsonPath("$.message").value("Service temporarily down"));
        }
    }

    @Nested
    @DisplayName("Обработка MethodArgumentNotValidException")
    class ValidationExceptionHandling {

        @Test
        @DisplayName("При невалидном URL возвращает 400 с деталями ошибок валидации")
        void givenInvalidUrl_whenRequest_thenReturnsValidationErrors() throws Exception {
            mockMvc.perform(post("/url")
                            .header("x-user-id", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"originalUrl\":\"invalid-url\"}"))
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.code").value("VALIDATION_FAILED"),
                            jsonPath("$.message").exists(),
                            jsonPath("$.details").isArray(),
                            jsonPath("$.details[0]").value(containsString("originalUrl")),
                            jsonPath("$.details[0]").value(containsString("URL"))
                    );
        }
    }

    @Nested
    @DisplayName("Обработка DataAccessException")
    class DataAccessExceptionHandling {

        @Test
        @DisplayName("Когда ошибка доступа к данным, тогда возвращает 500")
        void whenDatabaseError_thenReturnsInternalServerError() throws Exception {
            when(urlController.shortenUrl(any()))
                    .thenThrow(new DataAccessException("Connection failed") {
                    });

            mockMvc.perform(post("/url")
                            .header("x-user-id", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"originalUrl\":\"https://example.com\"}"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value("DATABASE_ERROR"));
        }
    }

    @Nested
    @DisplayName("Обработка UrlNotFoundException")
    class UrlNotFoundExceptionHandling {

        @Test
        @DisplayName("Когда URL не найден, тогда возвращает 404")
        void whenUrlNotFound_thenReturnsNotFound() throws Exception {
            when(urlController.redirectByHash(anyString()))
                    .thenThrow(new UrlNotFoundException("URL not found for hash: abc123"));

            mockMvc.perform(get("/url/abc123")
                            .header("x-user-id", 1))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("URL_NOT_FOUND"))
                    .andExpect(jsonPath("$.message").value("URL not found for hash: abc123"));
        }
    }

    @Nested
    @DisplayName("Обработка непредвиденных исключений возвращает 500 с правильной структурой")
    class ExceptionHandling {

        @Test
        void handleAllExceptions_WhenUnexpectedError_ReturnsInternalServerError() throws Exception {
            String errorMessage = "Test unexpected error";
            when(urlController.shortenUrl(any()))
                    .thenThrow(new RuntimeException(errorMessage));

            mockMvc.perform(post("/url")
                            .header("x-user-id", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"originalUrl\":\"https://example.com\"}"))
                    .andExpectAll(
                            status().isInternalServerError(),
                            jsonPath("$.code").value("INTERNAL_ERROR"),
                            jsonPath("$.message").value(errorMessage),
                            jsonPath("$.timestamp").exists()
                    );
        }
    }
}
