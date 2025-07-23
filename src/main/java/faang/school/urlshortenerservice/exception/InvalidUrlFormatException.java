package faang.school.urlshortenerservice.exception;

public class InvalidUrlFormatException extends RuntimeException {
    public InvalidUrlFormatException(String message) {
        super(message);
    }
}
