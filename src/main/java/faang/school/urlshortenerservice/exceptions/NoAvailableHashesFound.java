package faang.school.urlshortenerservice.exceptions;

public class NoAvailableHashesFound extends RuntimeException {
    public NoAvailableHashesFound(String message) {
        super(message);
    }
}
