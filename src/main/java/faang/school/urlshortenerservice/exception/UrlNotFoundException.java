package faang.school.urlshortenerservice.exception;

public class UrlNotFoundException extends BusinessValidationException {
    public UrlNotFoundException(String message) {
        super(message);
    }
}
