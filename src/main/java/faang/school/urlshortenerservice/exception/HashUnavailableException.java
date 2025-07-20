package faang.school.urlshortenerservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class HashUnavailableException extends RuntimeException {
    public HashUnavailableException() {
        super("Unable to generate a new short URL at this time");
    }
}
