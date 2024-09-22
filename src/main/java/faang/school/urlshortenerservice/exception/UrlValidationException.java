package faang.school.urlshortenerservice.exception;

public class UrlValidationException extends RuntimeException {

    public UrlValidationException(String message) {
        super(message);
    }
}
