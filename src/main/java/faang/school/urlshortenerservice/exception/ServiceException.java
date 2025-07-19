package faang.school.urlshortenerservice.exception;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {
    private final String errorCode;

    public ServiceException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}