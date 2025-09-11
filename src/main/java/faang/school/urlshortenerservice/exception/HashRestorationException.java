package faang.school.urlshortenerservice.exception;

public class HashRestorationException extends UrlCleanupException {

    public HashRestorationException(String message) {
        super(message);
    }

    public HashRestorationException(String message, Throwable cause) {
        super(message, cause);
    }
}