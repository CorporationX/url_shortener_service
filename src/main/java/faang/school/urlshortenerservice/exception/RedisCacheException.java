package faang.school.urlshortenerservice.exception;

public class RedisCacheException extends RuntimeException {

    public RedisCacheException(String message, Throwable e) {
        super(message, e);
    }
}
