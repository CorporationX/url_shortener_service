package faang.school.urlshortenerservice.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends ApiException {
    public ResourceNotFoundException(String message, Object... args) {
        super(message, HttpStatus.BAD_REQUEST, args);
    }
}
