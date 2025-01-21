package faang.school.urlshortenerservice.exeption;

public class BadUrl extends RuntimeException {
    public BadUrl(String message) {
        super(message);
    }
}
