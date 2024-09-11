package faang.school.urlshortenerservice.exception.handler;

public class UrlNotFoundException extends RuntimeException{
    public UrlNotFoundException(String message) {
        super(message);
    }
}
