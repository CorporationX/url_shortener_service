package faang.school.urlshortenerservice.exception.custom;

public class DataNotFoundException extends RuntimeException {
    public DataNotFoundException() {
        super();
    }
    public DataNotFoundException(String message) {
        super(message);
    }
    public DataNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
