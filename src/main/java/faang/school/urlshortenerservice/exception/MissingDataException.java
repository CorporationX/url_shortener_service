package faang.school.urlshortenerservice.exception;

public class MissingDataException extends RuntimeException {
    public MissingDataException(String message) {
        super(message);
    }
}