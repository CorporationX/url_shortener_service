package faang.school.urlshortenerservice.handler.exception;

public class NotFoundElementException extends RuntimeException {
    public NotFoundElementException(String message) {
        super(message);
    }
}
