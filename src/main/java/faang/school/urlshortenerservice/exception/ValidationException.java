package faang.school.urlshortenerservice.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends ApiException {
    public ValidationException(String userMessage, String debugMessage) {
        super(HttpStatus.BAD_REQUEST, userMessage, debugMessage);
    }
}
