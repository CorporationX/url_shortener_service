package faang.school.urlshortenerservice.controller.handler;

import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.sql.SQLException;

@Getter
@AllArgsConstructor
public enum ExceptionMapping {
    URL_NOT_FOUND(UrlNotFoundException.class, HttpStatus.NOT_FOUND),
    SQL(SQLException.class, HttpStatus.INTERNAL_SERVER_ERROR),
    DATA_ACCESS(DataAccessException.class, HttpStatus.INTERNAL_SERVER_ERROR),
    ARG_NOT_VALID(MethodArgumentNotValidException.class, HttpStatus.BAD_REQUEST),
    DEFAULT(Exception.class, HttpStatus.INTERNAL_SERVER_ERROR);

    private final Class<? extends Exception> exceptionClass;
    private final HttpStatus httpStatus;

    public static HttpStatus getHttpStatus(Class<? extends Exception> exceptionClass) {
        for (ExceptionMapping exceptionMapping : ExceptionMapping.values()) {
            if (exceptionMapping.getExceptionClass().equals(exceptionClass)) {
                return exceptionMapping.getHttpStatus();
            }
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
