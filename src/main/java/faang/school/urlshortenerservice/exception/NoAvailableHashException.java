package faang.school.urlshortenerservice.exception;

public class NoAvailableHashException extends RuntimeException {
    public NoAvailableHashException(String message) {
        super(message);
    }

    public NoAvailableHashException(String message, Throwable cause) {
        super(message, cause);
    }
}

