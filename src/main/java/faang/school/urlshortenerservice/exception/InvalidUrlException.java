package faang.school.urlshortenerservice.exception;

public class InvalidUrlException extends RuntimeException {
    public InvalidUrlException(String message) {
        super(message + "\n Example: https://www.google.com");
    }
}
