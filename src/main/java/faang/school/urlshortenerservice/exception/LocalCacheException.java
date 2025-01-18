package faang.school.urlshortenerservice.exception;

public class LocalCacheException extends RuntimeException {
    public LocalCacheException(String message) {
        super(message);
    }
}