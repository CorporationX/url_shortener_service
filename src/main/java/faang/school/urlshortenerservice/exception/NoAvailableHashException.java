package faang.school.urlshortenerservice.exception;

public class NoAvailableHashException extends NotFoundException {
    public NoAvailableHashException(String message) {
        super(message);
    }
}
