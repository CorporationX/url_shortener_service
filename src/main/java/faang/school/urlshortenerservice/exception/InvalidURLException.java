package faang.school.urlshortenerservice.exception;

public class InvalidURLException extends RuntimeException {
    public InvalidURLException(String message) {
        super(message);
    }
}