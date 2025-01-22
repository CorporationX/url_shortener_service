package faang.school.urlshortenerservice.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NonUniqueResultException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Illegal argument Exception", e);
        return buildExceptionDto(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleEntityNotFoundException(EntityNotFoundException e) {
        log.warn("entity not found exception", e);
        return buildExceptionDto(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(NonUniqueResultException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail handleNonUniqueResultException(NonUniqueResultException e) {
        log.error("non unique exception", e);
        return buildExceptionDto(HttpStatus.CONFLICT, "Can't find correct url");
    }

    @ExceptionHandler(InvalidUrlException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleNonUniqueResultException(InvalidUrlException e) {
        log.error("non unique exception", e);
        return buildExceptionDto(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    private ProblemDetail handleDataAccessException(DataAccessException e) {
        log.error("data access exception", e);
        return buildExceptionDto(HttpStatus.BAD_GATEWAY, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ProblemDetail handleGeneralException(Exception e) {
        log.error("Unknown exception", e);
        return buildExceptionDto(HttpStatus.INTERNAL_SERVER_ERROR, "something went wrong: " + e.getMessage());
    }

    private ProblemDetail buildExceptionDto(HttpStatus status, String description) {
        return ProblemDetail.forStatusAndDetail(status, description);
    }
}
