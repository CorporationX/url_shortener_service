package faang.school.urlshortenerservice.exceptions;

public class HashCacheInitializationException extends RuntimeException {
    public HashCacheInitializationException(String message) {
        super(message);
    }
    public HashCacheInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
