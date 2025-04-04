package faang.school.urlshortenerservice.exception;

public class UrlNotFoundException extends RuntimeException {
    public UrlNotFoundException(String shortLink) {
        super(String.format("Url for short link %s not found", shortLink));
    }
}
