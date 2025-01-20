package faang.school.urlshortenerservice.exception;

public class RedissonException extends RuntimeException {

    public RedissonException(String message, Exception e) {
        super(message, e);
    }
}
