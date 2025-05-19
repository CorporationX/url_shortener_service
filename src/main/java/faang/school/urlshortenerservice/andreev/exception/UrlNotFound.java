package faang.school.urlshortenerservice.andreev.exception;

public class UrlNotFound extends RuntimeException {
    public UrlNotFound(String message) {
        super(message);
    }
}
