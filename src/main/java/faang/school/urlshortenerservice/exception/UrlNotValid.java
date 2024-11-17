package faang.school.urlshortenerservice.exception;

public class UrlNotValid extends RuntimeException {
    public UrlNotValid(String message) {
        super(message);
    }
}
