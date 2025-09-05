package faang.school.urlshortenerservice.exception;

import jakarta.persistence.EntityNotFoundException;

public class HashNotFoundException extends EntityNotFoundException {
    public HashNotFoundException(String msg) {
        super(msg);
    }
}