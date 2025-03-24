package faang.school.urlshortenerservice.exception;

public class BadUrlException extends RuntimeException {
    public BadUrlException(String message) {
        super(message);
    }
}
