package faang.school.urlshortenerservice.exception;

public class NoSuchShortUrlException extends RuntimeException {
    public NoSuchShortUrlException(String hashNotFound) {
    }
}
