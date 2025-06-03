package faang.school.urlshortenerservice.ExceptionHandler;

import lombok.Getter;

@Getter
public class UrlNotFoundException extends RuntimeException {

    private final Errors error;

    public UrlNotFoundException(String message, Errors error) {
        super(message);
        this.error = error;

    }
}
