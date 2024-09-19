package faang.school.urlshortenerservice.exception;

public class UrlNotFoundException extends RuntimeException {
    public UrlNotFoundException(String hash) {
        super("Url not found for hash: " + hash);
    }
}
