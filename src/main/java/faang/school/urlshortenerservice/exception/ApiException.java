package faang.school.urlshortenerservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class ApiException extends RuntimeException {
    private final HttpStatus status;

    protected ApiException(String message) {
        super(message);
        this.status = getDefaultStatus();
    }

    protected abstract HttpStatus getDefaultStatus();
}
