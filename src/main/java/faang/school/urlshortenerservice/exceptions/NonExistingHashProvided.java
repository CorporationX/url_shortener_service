package faang.school.urlshortenerservice.exceptions;

public class NonExistingHashProvided extends RuntimeException {
    public NonExistingHashProvided(String message) {
        super(message);
    }
}
