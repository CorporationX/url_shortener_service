package faang.school.urlshortenerservice.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends ApiException {
    private static final String MESSAGE = "Url по переданному хешу: %s не найден";

    public ResourceNotFoundException(Object... args) {
        super(MESSAGE, HttpStatus.NOT_FOUND, args);
    }
}