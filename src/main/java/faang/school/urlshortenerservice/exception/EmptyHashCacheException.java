package faang.school.urlshortenerservice.exception;

/**
 * @author Evgenii Malkov
 */
public class EmptyHashCacheException extends RuntimeException {

    public EmptyHashCacheException(String message) {
        super(message);
    }
}
