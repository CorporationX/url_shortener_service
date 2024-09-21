package faang.school.urlshortenerservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class UrlDataValidationException extends RuntimeException {
    public UrlDataValidationException(String message) {
        super(message);
    }
}
