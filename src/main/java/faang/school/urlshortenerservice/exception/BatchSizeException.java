package faang.school.urlshortenerservice.exception;

public class BatchSizeException extends RuntimeException {
    public BatchSizeException(String message) {
        super(message);
    }
}
