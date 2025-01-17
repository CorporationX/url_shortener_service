package faang.school.urlshortenerservice.exception;

import faang.school.urlshortenerservice.dto.exception.ExceptionDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NonUniqueResultException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Illegal argument Exception", e);
        return bulidExceptionDto(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("entity not found exception", e);
        return bulidExceptionDto(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(NonUniqueResultException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDto handleNonUniqueResultException(NonUniqueResultException e) {
        log.error("non unique exception", e);
        return bulidExceptionDto(HttpStatus.CONFLICT, "Can't find correct url");
    }

    @ExceptionHandler(InvalidUrlException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleNonUniqueResultException(InvalidUrlException e) {
        log.error("non unique exception", e);
        return bulidExceptionDto(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    private ExceptionDto handleDataAccessException(DataAccessException e) {
        log.error("data access exception", e);
        return bulidExceptionDto(HttpStatus.BAD_GATEWAY, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ExceptionDto handleGeneralException(Exception e) {
        log.error("Unknown exception", e);
        return bulidExceptionDto(HttpStatus.INTERNAL_SERVER_ERROR, "something went wrong: " + e.getMessage());
    }

    private ExceptionDto bulidExceptionDto(HttpStatus status, String description) {
        return ExceptionDto.builder()
                .statusCode(status.value())
                .status(status)
                .description(description)
                .build();
    }
}
