package faang.school.urlshortenerservice.exception;

public class HashCacheIsEmptyException extends RuntimeException {

    public HashCacheIsEmptyException(String message) {
        super(message);
    }
}
