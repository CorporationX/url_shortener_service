package faang.school.urlshortenerservice.exception;

public class URLNotFoundException extends RuntimeException {
    public URLNotFoundException(String message) {
        super(message);
    }
}
