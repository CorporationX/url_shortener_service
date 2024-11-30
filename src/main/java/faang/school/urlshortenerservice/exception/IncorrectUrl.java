package faang.school.urlshortenerservice.exception;

public class IncorrectUrl extends RuntimeException {
    public IncorrectUrl(String message) {
        super(message);
    }
}
