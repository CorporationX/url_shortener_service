package faang.school.urlshortenerservice.exception;

public class NoFreeHashesException extends RuntimeException {
    public NoFreeHashesException(String message) {
        super(message);
    }
}