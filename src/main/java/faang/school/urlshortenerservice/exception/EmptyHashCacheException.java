package faang.school.urlshortenerservice.exception;

public class EmptyHashCacheException extends RuntimeException {

    public EmptyHashCacheException(String message) {
        super(message);
    }
}
