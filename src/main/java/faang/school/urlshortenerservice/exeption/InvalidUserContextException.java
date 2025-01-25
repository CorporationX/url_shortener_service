package faang.school.urlshortenerservice.exeption;

public class InvalidUserContextException extends RuntimeException {
    public InvalidUserContextException(String message) {
        super(message);
    }

    public InvalidUserContextException(String message, Throwable cause) {
        super(message, cause);
    }
}
