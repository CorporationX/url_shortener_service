package faang.school.urlshortenerservice.exception;

public class HashException extends RuntimeException {
    public HashException(String message, Throwable e) {
        super(message, e);
    }
}
