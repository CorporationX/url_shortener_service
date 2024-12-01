package faang.school.urlshortenerservice.exeption.handler;

import faang.school.urlshortenerservice.entity.ErrorResponse;
import faang.school.urlshortenerservice.exeption.validation.DataValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;

@Slf4j
@RestControllerAdvice
public class UrlExceptionHandler {

    @Value("${services.url-shortener-service.name}")
    private String serviceName;

    @ExceptionHandler(HttpServerErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleHttpServerErrorException(Exception e) {
        log.error("Something gone wrong with a server: {}", e.getMessage());
        return ErrorResponse.builder()
                .serviceName(serviceName)
                .globalMessage(e.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();
    }

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidationException(DataValidationException e) {
        log.error("Data is not valid: {}", e.getMessage());
        return ErrorResponse.builder().
                serviceName(serviceName)
                .globalMessage(e.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
    }
}
