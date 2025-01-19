package faang.school.urlshortenerservice.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = org.springframework.http.HttpStatus.NOT_FOUND)
public class NoCacheFoundException extends RuntimeException {

    public NoCacheFoundException(String message) {
        super(message);
    }
}
