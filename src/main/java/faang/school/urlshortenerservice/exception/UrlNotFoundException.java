package faang.school.urlshortenerservice.exception;

/**
 * @author Alexander Bulgakov
 */
public class UrlNotFoundException extends RuntimeException {
    public UrlNotFoundException(String message) {
        super(message);
    }
}
