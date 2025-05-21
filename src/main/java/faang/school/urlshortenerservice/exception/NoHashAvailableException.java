package faang.school.urlshortenerservice.exception;

public class NoHashAvailableException extends RuntimeException {
    public NoHashAvailableException(String message) {
        super(message);
    }

    public NoHashAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
