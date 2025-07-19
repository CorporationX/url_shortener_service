package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import faang.school.urlshortenerservice.dto.ErrorResponseDto;
import faang.school.urlshortenerservice.exception.InvalidUrlFormatException;
import faang.school.urlshortenerservice.exception.UnauthorizedException;
import faang.school.urlshortenerservice.exception.UrlNotFound;
import faang.school.urlshortenerservice.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <h2>Задание</h2>
 * <div>Добавить глобальный обработчик исключений в URL Shortener, который будет ловить разнообразные
 * ошибки, что могут возникнуть в системе и превращать их в http-ответы пользователю с соответствующими статусами</div>
 * <h2>Критерии приема</h2>
 * <li>UrlExceptionHandler — Spring bean и controller advice.</li>
 * <li>Внутренние ошибки системы превращаются в ответ со статусом 500 и соответствующим сообщением.</li>
 * <li>Ошибки валидации превращаются в ответ со статусом Bad Request и соответствующим сообщением.</li>
 * <li>Есть дефолтный обработчик всех исключений, что не попадают под специфичные обработчики.
 * Возвращают статус 500 и сообщение об ошибке общего формата.</li>
 * <li>Используются аннотации lombok.</li>
 * <li>Отсутствует дубликация кода.</li>
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class UrlExceptionHandler {
    public static final String RUNTIME_ERROR = "Runtime error, see log";
    private final Utils utils;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> detail = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                error -> Objects.requireNonNullElse(error.getDefaultMessage(), "")
            ));
        String errorMessage = utils.format("Validation failed with {} errors",
            e.getBindingResult().getFieldErrors().size());
        return getResponse("handlerMethodArgumentNotValidException", errorMessage, detail, e);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorResponseDto handlerMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return getResponse("handlerMethodArgumentTypeMismatchException", e);
    }

    @ExceptionHandler(UnrecognizedPropertyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handlerUnrecognizedPropertyException(UnrecognizedPropertyException e) {
        return getResponse("handleUnrecognizedPropertyException", e);
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseDto handlerUnauthorizedException(UnauthorizedException e) {
        return getResponse("handlerUnauthorizedException", e);
    }

    @ExceptionHandler(UrlNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handlerUrlNotFound(UrlNotFound e) {
        return getResponse("handlerUrlNotFound", e);
    }

    @ExceptionHandler(InvalidUrlFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handlerInvalidUrlFormatException(InvalidUrlFormatException e) {
        return getResponse("handlerInvalidUrlFormatException", e);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handlerRuntimeException(RuntimeException e) {
        return getResponse("handlerRuntimeException", RUNTIME_ERROR, e);
    }

    private ErrorResponseDto getResponse(String exceptionLabel, Exception e) {
        log.error("{}: {}", exceptionLabel, e.getMessage(), e);
        return new ErrorResponseDto(e.getMessage());
    }

    private ErrorResponseDto getResponse(String exceptionLabel, String errorMessage, Exception e) {
        log.error("{}: {}", exceptionLabel, e.getMessage(), e);
        return new ErrorResponseDto(errorMessage);
    }

    private ErrorResponseDto getResponse(
        String exceptionLabel,
        String errorMessage,
        Map<String, String> detail,
        Exception e
    ) {
        log.error("{}: {}", exceptionLabel, e.getMessage(), e);
        return new ErrorResponseDto(errorMessage, detail);
    }
}
