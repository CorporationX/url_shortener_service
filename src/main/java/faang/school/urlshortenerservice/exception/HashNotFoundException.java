package faang.school.urlshortenerservice.exception;

public class HashNotFoundException extends RuntimeException {
    public HashNotFoundException(String message) {
        super(message);
    }

    public HashNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public HashNotFoundException(Throwable cause) {
        super(cause);
    }
}
