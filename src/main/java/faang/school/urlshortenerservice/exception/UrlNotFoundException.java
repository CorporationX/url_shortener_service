package faang.school.urlshortenerservice.exception;

public class UrlNotFoundException extends RuntimeException {

    public final String nonExistentUrl;

    public UrlNotFoundException(String nonExistentUrl) {
        super(
                String.format("Original URL not found by shortened: %s", nonExistentUrl)
        );
        this.nonExistentUrl = nonExistentUrl;
    }
}
