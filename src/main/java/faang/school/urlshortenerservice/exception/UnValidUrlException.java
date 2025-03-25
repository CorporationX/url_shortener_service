package faang.school.urlshortenerservice.exception;

public class UnValidUrlException extends RuntimeException {
    public UnValidUrlException(String message) {
        super(message);
    }
}
