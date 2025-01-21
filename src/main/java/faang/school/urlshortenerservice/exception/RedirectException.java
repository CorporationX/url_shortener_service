package faang.school.urlshortenerservice.exception;

public class RedirectException extends RuntimeException {
    public RedirectException(String message) {
        super(message);
    }
}