package faang.school.urlshortenerservice.exeption.validation;

public class DataValidationException extends RuntimeException {
    public DataValidationException(String message) {
        super(message);
    }
}
