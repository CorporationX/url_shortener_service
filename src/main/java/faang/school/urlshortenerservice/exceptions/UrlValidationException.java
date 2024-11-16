package faang.school.urlshortenerservice.exceptions;

public class UrlValidationException extends RuntimeException {
    public UrlValidationException(String message) {
        super(message);
    }
}
