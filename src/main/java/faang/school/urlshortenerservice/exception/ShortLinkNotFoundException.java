package faang.school.urlshortenerservice.exception;

public class ShortLinkNotFoundException extends RuntimeException{
    public ShortLinkNotFoundException(String message) {
        super(message);
    }
}
