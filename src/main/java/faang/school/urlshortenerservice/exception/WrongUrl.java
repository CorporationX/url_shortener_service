package faang.school.urlshortenerservice.exception;

public class WrongUrl extends RuntimeException {
    public WrongUrl(String message) {
        super(message);
    }
}
