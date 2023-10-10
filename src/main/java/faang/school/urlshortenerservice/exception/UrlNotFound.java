package faang.school.urlshortenerservice.exception;

public class UrlNotFound extends RuntimeException {

    public UrlNotFound() {
        super("Url not found");
    }
}
