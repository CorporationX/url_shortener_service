package faang.school.urlshortenerservice.exceptions;

public class UrlNotFoundException extends RuntimeException {

    public UrlNotFoundException(String message) {
        super(message);
    }
}
