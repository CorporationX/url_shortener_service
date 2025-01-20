package faang.school.urlshortenerservice.exception;

import lombok.Getter;

@Getter
public class UrlNotFoundException extends RuntimeException {
    public UrlNotFoundException(String message) {
        super(message);
    }
}