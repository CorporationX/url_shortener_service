package faang.school.urlshortenerservice.exception;

public class MalformedUrlException extends RuntimeException {
    public MalformedUrlException(String message) {
        super(message);
    }
}
