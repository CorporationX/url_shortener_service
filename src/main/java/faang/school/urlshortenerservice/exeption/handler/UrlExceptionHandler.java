package faang.school.urlshortenerservice.exeption.handler;

import faang.school.urlshortenerservice.entity.ErrorResponse;
import faang.school.urlshortenerservice.exeption.validation.DataValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class UrlExceptionHandler {

    @Value("${services.url-shortener-service.name}")
    private String serviceName;

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(Exception e) {
        log.info("Exception found and occurred: {}", e.getMessage());
        return ErrorResponse.builder()
                .serviceName(serviceName)
                .globalMessage(e.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();
    }

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidationException(DataValidationException e) {
        log.info("Exception found and occurred: {}", e.getMessage());
        return ErrorResponse.builder().
                serviceName(serviceName)
                .globalMessage(e.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
    }
}
