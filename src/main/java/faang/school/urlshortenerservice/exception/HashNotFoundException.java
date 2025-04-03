package faang.school.urlshortenerservice.exception;

public class HashNotFoundException extends ResourceNotFoundException {
    public HashNotFoundException(String message) {
        super(message);
    }
}
