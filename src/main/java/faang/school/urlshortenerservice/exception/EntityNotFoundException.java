package faang.school.urlshortenerservice.exception;

import org.webjars.NotFoundException;

public class EntityNotFoundException extends NotFoundException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}