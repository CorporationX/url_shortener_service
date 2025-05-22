package faang.school.urlshortenerservice.exceptions;

public class EmptyNumbersListException extends RuntimeException {
    public EmptyNumbersListException(String message) {
        super(message);
    }

    public EmptyNumbersListException(String message, Throwable cause) {
        super(message, cause);
    }
}
