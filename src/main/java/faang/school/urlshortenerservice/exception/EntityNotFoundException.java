package faang.school.urlshortenerservice.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntityNotFoundException extends IllegalArgumentException {
    public EntityNotFoundException(String message){
        super(message);
        log.error(message);
    }
}
