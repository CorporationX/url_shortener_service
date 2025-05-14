package faang.school.urlshortenerservice.exception;

public class HashNotFoundException extends RuntimeException {

    public HashNotFoundException(String message, Object... args) {
        super(String.format(message, args));
    }
}
