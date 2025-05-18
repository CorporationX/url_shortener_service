package faang.school.urlshortenerservice.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ApiException {
    public NotFoundException(String userMessage) {
        super(HttpStatus.NOT_FOUND, userMessage, "Resource not found: " + userMessage);
    }
}
