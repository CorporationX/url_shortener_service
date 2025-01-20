package faang.school.urlshortenerservice.exception;

public class UrlExpiredException extends RuntimeException {
    private static final String RESOURCE_NOT_FOUND = "Your URL %s doesn't exit or already expired.";

    public UrlExpiredException(String url) {
        super(String.format(RESOURCE_NOT_FOUND, url));
    }
}
