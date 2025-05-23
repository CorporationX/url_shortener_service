package faang.school.urlshortenerservice.exception;

public class UrlNotFoundException extends RuntimeException {

    public UrlNotFoundException(String message, Object... args) {
        super(String.format(message, args));
    }

}
