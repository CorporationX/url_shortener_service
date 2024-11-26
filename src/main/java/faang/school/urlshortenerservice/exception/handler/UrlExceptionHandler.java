package faang.school.urlshortenerservice.exception.handler;

import faang.school.urlshortenerservice.exception.response.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class UrlExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("EntityNotFoundException occurred: {}, "
                + "service cannot find that!", e.getMessage(), e);
        return buildResponse(e);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(ValidationException e) {
        log.error("ValidationException occurred: {} , "
                + "please check your request some validation has failed!", e.getMessage(), e);
        return buildResponse(e);
    }

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleDaeException(DataAccessException dae) {
        log.error("Data Access Exception occurred! - {} ,"
                + "something went wrong while executing request, probably high load on server! "
                + "Please try again!", dae.getMessage(), dae);
        return buildResponse(dae);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception e) {
        log.error("Exception on server side occurred! {} , please check logs for more info!", e.getMessage(), e);
        return buildResponse(e);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException mae) {
        log.error("MethodArgumentNotValidException occurred: {} , "
                + "please check your request some arguments are invalid!", mae.getMessage(), mae);
        return buildResponse(mae);
    }

    private ErrorResponse buildResponse(Exception exception) {
        return ErrorResponse.builder()
                .errorMessage(exception.getMessage())
                .timeStamp(LocalDateTime.now())
                .build();
    }
}
