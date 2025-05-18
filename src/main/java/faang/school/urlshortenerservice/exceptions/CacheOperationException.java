package faang.school.urlshortenerservice.exceptions;

public class CacheOperationException extends RuntimeException {
    public CacheOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
