package faang.school.urlshortenerservice.exception;

public class UrlNotValidException extends RuntimeException {
    public UrlNotValidException(String message) {
        super(message);
    }
}
