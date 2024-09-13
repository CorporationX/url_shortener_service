package faang.school.urlshortenerservice.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntityNotFoundException extends RuntimeException {
    public <T> EntityNotFoundException(Class<T> entityType, Object id) {
        super("Entity of type: %s with id: %s not found.".formatted(entityType, id));
        log.error(this.getMessage(), this);
    }
}