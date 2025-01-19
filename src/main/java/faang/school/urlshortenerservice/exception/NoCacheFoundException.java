package faang.school.urlshortenerservice.exception;

public class NoCacheFoundException extends RuntimeException {
    public NoCacheFoundException(String message) {
        super(message);
    }
}
