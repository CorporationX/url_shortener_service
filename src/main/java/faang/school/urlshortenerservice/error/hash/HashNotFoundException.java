package faang.school.urlshortenerservice.error.hash;

import jakarta.persistence.EntityNotFoundException;

public class HashNotFoundException extends EntityNotFoundException {

    public HashNotFoundException(String msg) {
        super(msg);
    }
}
