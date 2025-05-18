package faang.school.urlshortenerservice.exceptions;

public class CleanUrlException extends RuntimeException{
    public CleanUrlException(String message) {
        super(message);
    }
    public CleanUrlException(String message, Throwable cause) {
        super(message, cause);
    }
}
