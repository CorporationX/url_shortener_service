package faang.school.urlshortenerservice.exeption;

public class LocalCacheException extends RuntimeException {
    public LocalCacheException(String message) {
        super(message);
    }
}
