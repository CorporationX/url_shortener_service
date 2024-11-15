package faang.school.urlshortenerservice.exception;

public class UrlNotRecognizedException extends RuntimeException {
    public UrlNotRecognizedException() {
    }

    public UrlNotRecognizedException(String message) {
        super(message);
    }
}
