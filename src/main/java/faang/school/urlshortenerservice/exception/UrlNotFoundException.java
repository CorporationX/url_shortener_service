package faang.school.urlshortenerservice.exception;

import org.springframework.http.HttpStatus;

public class UrlNotFoundException extends ApiException {
    public UrlNotFoundException(String userMessage) {
        super(HttpStatus.NOT_FOUND, userMessage, "Resource not found: " + userMessage);
    }
}
