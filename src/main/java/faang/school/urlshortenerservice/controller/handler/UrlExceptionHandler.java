package faang.school.urlshortenerservice.controller.handler;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Глобальный обработчик исключений для контроллеров, связанных с URL.
 * Обрабатывает исключения и возвращает стандартизированный ответ в формате {@link ErrorResponse}.
 */
@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class UrlExceptionHandler {

    private final MessageSource messageSource;

    /**
     * Обрабатывает исключение {@link EntityNotFoundException}.
     *
     * @param e       Исключение {@link EntityNotFoundException}.
     * @param request HTTP-запрос, в котором произошло исключение.
     * @return Ответ с информацией об ошибке в формате {@link ErrorResponse}.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException e, HttpServletRequest request) {
        return buildErrorResponse(
            request.getRequestURI(),
            HttpStatus.BAD_REQUEST.value(),
            getErrorAndLog("exception.entity_not_found", e),
            e.getMessage(),
            LocalDateTime.now());
    }

    /**
     * Обрабатывает исключение {@link ValidationException}.
     *
     * @param e       Исключение {@link ValidationException}.
     * @param request HTTP-запрос, в котором произошло исключение.
     * @return Ответ с информацией об ошибке в формате {@link ErrorResponse}.
     */
    @ExceptionHandler(ValidationException.class)
    public ErrorResponse handleValidationException(ValidationException e, HttpServletRequest request) {
        return buildErrorResponse(
            request.getRequestURI(),
            HttpStatus.BAD_REQUEST.value(),
            getErrorAndLog("error.validation_exception", e),
            e.getMessage(),
            LocalDateTime.now());
    }

    /**
     * Обрабатывает все остальные исключения, которые не были перехвачены другими обработчиками.
     *
     * @param e       Исключение.
     * @param request HTTP-запрос, в котором произошло исключение.
     * @return Ответ с информацией об ошибке в формате {@link ErrorResponse}.
     */
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(Exception e, HttpServletRequest request) {
        return buildErrorResponse(
            request.getRequestURI(),
            500,
            getErrorAndLog("error.exception", e),
            e.getMessage(),
            LocalDateTime.now());
    }

    /**
     * Логирует ошибку и возвращает сообщение об ошибке из ресурсов.
     *
     * @param msgProperty Ключ сообщения об ошибке в ресурсах.
     * @param e           Исключение.
     * @return Сообщение об ошибке.
     */
    private String getErrorAndLog(String msgProperty, Exception e) {
        String error = messageSource.getMessage(msgProperty, null, LocaleContextHolder.getLocale());
        log.error(error, e);
        return error;
    }

    /**
     * Создает объект {@link ErrorResponse} с информацией об ошибке.
     *
     * @param url       URL запроса, в котором произошла ошибка.
     * @param status    HTTP-статус ошибки.
     * @param error     Тип ошибки.
     * @param message   Сообщение об ошибке.
     * @param timestamp Время возникновения ошибки.
     * @return Объект {@link ErrorResponse}.
     */
    private ErrorResponse buildErrorResponse(String url, int status, String error,
                                             String message, LocalDateTime timestamp) {
        return ErrorResponse.builder()
            .url(url)
            .status(status)
            .error(error)
            .message(message)
            .timestamp(timestamp)
            .build();
    }
}
