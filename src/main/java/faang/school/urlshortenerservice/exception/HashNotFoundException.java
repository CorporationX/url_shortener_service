package faang.school.urlshortenerservice.exception;

public class HashNotFoundException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Hash not found";
    public HashNotFoundException() {
        super(DEFAULT_MESSAGE);
    }
}
