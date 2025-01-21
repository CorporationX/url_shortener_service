package faang.school.urlshortenerservice.exception;

public class UrlCreationException extends RuntimeException {
    public UrlCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
