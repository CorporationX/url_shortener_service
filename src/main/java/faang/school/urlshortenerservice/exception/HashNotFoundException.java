package faang.school.urlshortenerservice.exception;

import lombok.Getter;

@Getter
public class HashNotFoundException extends RuntimeException {
    public HashNotFoundException(String hash) {
        super("URL with hash '" + hash + "' not found");
    }
}