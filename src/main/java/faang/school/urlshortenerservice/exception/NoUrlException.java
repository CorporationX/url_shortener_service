package faang.school.urlshortenerservice.exception;

public class NoUrlException extends RuntimeException {
    public NoUrlException(String message) {
        super(message);
    }
}
