package faang.school.urlshortenerservice.exception;

public class ExceedHashDigitsLimitException extends RuntimeException {
    public ExceedHashDigitsLimitException(String message) {
        super(message);
    }
}
