package faang.school.urlshortenerservice.exception;

public class TemporarilyUnavailableException extends RuntimeException {
    public TemporarilyUnavailableException(String message) {
        super(message);
    }
}
