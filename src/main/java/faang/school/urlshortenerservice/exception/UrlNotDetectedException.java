package faang.school.urlshortenerservice.exception;

public class UrlNotDetectedException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Url not detected";
    public UrlNotDetectedException() {
        super(DEFAULT_MESSAGE);
    }
}
