package faang.school.urlshortenerservice.exception;

import lombok.Getter;

@Getter
public class HashNotFoundException extends RuntimeException {
    private final String hash;

    public HashNotFoundException(String message, String hash) {
        super(message);
        this.hash = hash;
    }
}
