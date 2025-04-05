package faang.school.urlshortenerservice.exception;

import lombok.Getter;

@Getter
public class UrlNotFoundException extends RuntimeException {
    private final String hash;

    public UrlNotFoundException(String hash) {
        super("URL with hash " + hash + " not found");
        this.hash = hash;
    }
}
