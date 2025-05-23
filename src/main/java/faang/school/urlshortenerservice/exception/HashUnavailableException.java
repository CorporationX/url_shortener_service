package faang.school.urlshortenerservice.exception;

public class HashUnavailableException extends RuntimeException {
    public HashUnavailableException(String message) {
        super(message);
    }

    public HashUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
