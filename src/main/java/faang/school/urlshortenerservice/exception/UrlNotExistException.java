package faang.school.urlshortenerservice.exception;

public class UrlNotExistException extends RuntimeException {
    public UrlNotExistException(String message) {
        super(message);
    }
}
