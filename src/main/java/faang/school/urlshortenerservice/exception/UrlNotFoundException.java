package faang.school.urlshortenerservice.exception;

public class UrlNotFoundException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Url not found";
    public UrlNotFoundException() {
        super(DEFAULT_MESSAGE);
    }
}