package faang.school.urlshortenerservice.exception.handler;

public class DataValidationException extends RuntimeException{
    public DataValidationException(String message) {
        super(message);
    }
}
