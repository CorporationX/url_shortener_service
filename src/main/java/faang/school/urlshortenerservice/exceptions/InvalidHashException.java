package faang.school.urlshortenerservice.exceptions;

public class InvalidHashException extends RuntimeException {
    public InvalidHashException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidHashException(String message) {
        super(message);
    }
}
