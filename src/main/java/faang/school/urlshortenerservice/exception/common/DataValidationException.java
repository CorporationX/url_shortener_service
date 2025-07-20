package faang.school.urlshortenerservice.exception.common;

public class DataValidationException extends RuntimeException {
    public DataValidationException(String message) {
        super(message);
    }
}