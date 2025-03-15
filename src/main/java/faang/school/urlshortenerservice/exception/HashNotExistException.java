package faang.school.urlshortenerservice.exception;

public class HashNotExistException extends RuntimeException {
    public HashNotExistException(String message) {
        super(message);
    }
}
