package faang.school.urlshortenerservice.exception;

public class HashCacheException extends RuntimeException {

    public HashCacheException(String message) {
        super(message);
    }

    public HashCacheException(Throwable cause) {
        super(cause);
    }

    public HashCacheException(String message, Throwable cause) {
        super(message, cause);
    }
}
