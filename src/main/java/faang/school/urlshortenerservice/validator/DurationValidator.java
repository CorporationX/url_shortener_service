package faang.school.urlshortenerservice.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class DurationValidator {

    public void validateDurationIsNotNull(Duration duration){
        if (duration == null) {
            RuntimeException exception = new IllegalArgumentException("Duration is Null");
            log.error(exception.getMessage(), exception);
            throw exception;
        }
    }
}
