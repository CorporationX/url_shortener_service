package faang.school.urlshortenerservice.exception;

public class TimeOutException extends RuntimeException {
    public TimeOutException(String message) {
        super(message);
    }
}
