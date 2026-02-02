package faang.school.urlshortenerservice.exception;

public class UrlNotValidException extends BusinessValidationException {
    public UrlNotValidException(String message) {
        super(message);
    }
}