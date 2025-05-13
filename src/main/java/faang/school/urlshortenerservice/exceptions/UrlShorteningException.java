package faang.school.urlshortenerservice.exceptions;

public class UrlShorteningException extends RuntimeException{
    public UrlShorteningException(String message) {
        super(message);
    }
    public UrlShorteningException(String message, Throwable cause) {
        super(message, cause);
    }
}
