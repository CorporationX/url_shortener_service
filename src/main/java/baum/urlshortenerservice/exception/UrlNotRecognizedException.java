package baum.urlshortenerservice.exception;

public class UrlNotRecognizedException extends RuntimeException {
    public UrlNotRecognizedException(String message) {
        super(message);
    }
}
