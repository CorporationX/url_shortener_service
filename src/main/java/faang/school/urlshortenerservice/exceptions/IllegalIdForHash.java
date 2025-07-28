package faang.school.urlshortenerservice.exceptions;

public class IllegalIdForHash extends RuntimeException {
    public IllegalIdForHash(String message) {
        super(message);
    }
}
