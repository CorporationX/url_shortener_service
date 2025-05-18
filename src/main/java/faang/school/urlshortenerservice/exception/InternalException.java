package faang.school.urlshortenerservice.exception;

import org.springframework.http.HttpStatus;

public class InternalException extends ApiException {
    public InternalException(String debugMessage) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", debugMessage);
    }
}
