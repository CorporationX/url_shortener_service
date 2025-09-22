package faang.school.urlshortenerservice.exception.common;

public class PreConditionFailedException extends RuntimeException {
    public PreConditionFailedException(String message) {
        super(message);
    }
}