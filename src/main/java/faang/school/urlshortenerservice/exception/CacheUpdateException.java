package faang.school.urlshortenerservice.exception;

public class CacheUpdateException extends RuntimeException {
    public CacheUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
