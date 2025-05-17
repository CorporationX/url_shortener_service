package faang.school.urlshortenerservice.handler;

public class UrlNotFoundException extends  RuntimeException{
    public UrlNotFoundException(String message) {
        super(message);
    }
}
