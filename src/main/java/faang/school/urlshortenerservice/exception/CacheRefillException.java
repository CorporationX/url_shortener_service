package faang.school.urlshortenerservice.exception;

public class CacheRefillException extends RuntimeException {
    public CacheRefillException(String message) {
        super(message);
    }
}
