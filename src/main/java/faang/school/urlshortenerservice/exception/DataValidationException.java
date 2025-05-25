package faang.school.urlshortenerservice.exception;

public class DataValidationException extends RuntimeException {

    public DataValidationException(String message, Object... args) {
        super(String.format(message, args));
    }
}
