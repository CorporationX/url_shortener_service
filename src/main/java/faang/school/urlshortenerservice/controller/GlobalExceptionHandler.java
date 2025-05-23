package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.ErrorResponse;
import faang.school.urlshortenerservice.exception.HashUnavailableException;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Обрабатывает исключение {@link HashUnavailableException},
     * возникающее при временной недоступности сервиса генерации хэшей.
     *
     * @param exception Перехваченное исключение {@link HashUnavailableException}
     * @return Ответ с HTTP статусом 503 и структурированным описанием ошибки
     */
    @ExceptionHandler(HashUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleHashUnavailable(HashUnavailableException exception) {
        log.error("Unable to generate hash due to unavailability.", exception);
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse("HASH_UNAVAILABLE", exception.getMessage()));
    }

    /**
     * Обрабатывает исключение {@link MethodArgumentNotValidException},
     * возникающее при невалидных входных данных в запросе.
     * Формирует структурированный ответ с перечнем ошибок валидации.
     *
     * @param exception Перехваченное исключение {@link MethodArgumentNotValidException}
     * @return Ответ с HTTP статусом 400 и детализированным описанием ошибок валидации
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException exception) {
        List<String> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                .toList();

        log.error("Validation failed for request: {}", errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        "VALIDATION_FAILED",
                        exception.getMessage(),
                        errors));
    }

    /**
     * Обрабатывает исключения доступа к данным (БД, Redis и т.д.).
     * Логирует технические детали, но возвращает клиенту унифицированный ответ.
     *
     * @param exception Перехваченное исключение {@link DataAccessException}
     * @return Ответ с HTTP-статусом 500 и структурированным описанием ошибки
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(DataAccessException exception) {
        log.error("Data access error", exception);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        "DATABASE_ERROR",
                        exception.getMessage()));
    }

    /**
     * Обрабатывает исключение {@link UrlNotFoundException},
     * возникающее при попытке доступа к несуществующему URL.
     *
     * @param exception Перехваченное исключение {@link UrlNotFoundException}
     * @return Ответ с HTTP статусом 404 и структурированным описанием ошибки
     */
    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUrlNotFound(UrlNotFoundException exception) {
        log.error("Requested URL not found: {}", exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        "URL_NOT_FOUND",
                        exception.getMessage()));
    }

    /**
     * Глобальный обработчик для всех неперехваченных исключений.
     *
     * @param exception Исключение, которое не было обработано другими обработчиками
     * @return Ответ с HTTP-статусом 500 и JSON-объектом
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception exception) {
        log.error("Unexpected error", exception);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", exception.getMessage()));
    }
}
