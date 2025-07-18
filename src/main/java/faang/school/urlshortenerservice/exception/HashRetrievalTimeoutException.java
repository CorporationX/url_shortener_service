package faang.school.urlshortenerservice.exception;

public class HashRetrievalTimeoutException extends RuntimeException {
    public HashRetrievalTimeoutException() {
        super("Failed to retrieve a hash within the specified timeout. The service may be temporarily overloaded.");
    }
}
