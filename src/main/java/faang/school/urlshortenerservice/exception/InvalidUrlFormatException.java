package faang.school.urlshortenerservice.exception;

public class InvalidUrlFormatException extends RuntimeException {
    public InvalidUrlFormatException() {
        super("Invalid URL format. Please provide a valid URL.");
    }
}
