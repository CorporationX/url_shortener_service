package faang.school.urlshortenerservice.exception;

public class NoAvailableHashInCacheException extends RuntimeException {
    public NoAvailableHashInCacheException(String message) {
        super(message);
    }
}
