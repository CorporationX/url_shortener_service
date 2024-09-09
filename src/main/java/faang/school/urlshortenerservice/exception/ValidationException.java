package faang.school.urlshortenerservice.exception;

/**
 * @author Evgenii Malkov
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
