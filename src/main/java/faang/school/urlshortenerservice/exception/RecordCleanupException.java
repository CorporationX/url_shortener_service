package faang.school.urlshortenerservice.exception;

public class RecordCleanupException extends RuntimeException {

    public RecordCleanupException(String message) {
        super(message);
    }

    public RecordCleanupException(String message, Throwable cause) {
        super(message, cause);
    }
}
