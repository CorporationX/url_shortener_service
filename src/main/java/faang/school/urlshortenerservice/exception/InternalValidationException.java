package faang.school.urlshortenerservice.exception;

public class InternalValidationException extends RuntimeException {
    public InternalValidationException(String message) {
        super(message);
    }
}