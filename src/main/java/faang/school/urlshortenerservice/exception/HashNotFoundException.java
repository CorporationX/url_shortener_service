package faang.school.urlshortenerservice.exception;

public class HashNotFoundException extends RuntimeException {
    private static final String MESSAGE_TEMPLATE = "Hash '%s' not found";

    public HashNotFoundException(String hash) {
        super(String.format(MESSAGE_TEMPLATE, hash));
    }
}
