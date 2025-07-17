package faang.school.urlshortenerservice.exception.url;

import jakarta.persistence.EntityNotFoundException;

public class HashNotFoundException extends EntityNotFoundException {
    public HashNotFoundException(String msg) {
        super(msg);
    }
}
