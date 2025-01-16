package faang.school.urlshortenerservice.exception;

public class FullUrlNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Url for the passed hash: %s not found";

    public FullUrlNotFoundException(String hash) {
        super(String.format(MESSAGE, hash));
    }
}