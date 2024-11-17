package faang.school.urlshortenerservice.exception;

public class UrlNotfoundException extends RuntimeException {
    public UrlNotfoundException(String message, Object... args) {
        super(String.format(message, args));
    }
}
