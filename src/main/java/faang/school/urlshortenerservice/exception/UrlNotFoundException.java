package faang.school.urlshortenerservice.exception;

public class UrlNotFoundException extends RuntimeException {
    public UrlNotFoundException(String hash) {
        super(String.format("URL with hash '%s' not found", hash));
    }
}
