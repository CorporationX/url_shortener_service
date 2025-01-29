package faang.school.urlshortenerservice.exception;

import lombok.Getter;

@Getter
public class UrlNotFoundException extends RuntimeException {
    private final String hash;
    public UrlNotFoundException(String message, String hash) {
        super(message);
        this.hash = hash;
    }
}
