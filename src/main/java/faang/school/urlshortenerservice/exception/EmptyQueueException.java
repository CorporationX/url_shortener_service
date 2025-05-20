package faang.school.urlshortenerservice.exception;

public class EmptyQueueException extends RuntimeException{
    public EmptyQueueException(String message) {
        super(message);
    }
}
