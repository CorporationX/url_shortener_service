package faang.school.urlshortenerservice.exception;

public class SQLSaveException extends RuntimeException {
    public SQLSaveException(String message) {
        super(message);
    }
}
