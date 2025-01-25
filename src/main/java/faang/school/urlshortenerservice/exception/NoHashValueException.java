package faang.school.urlshortenerservice.exception;

public class NoHashValueException extends RuntimeException {
    public NoHashValueException(String message) {
        super(message);
    }
}
