package faang.school.urlshortenerservice.exception;

import lombok.Getter;

@Getter
public class DataValidationException extends RuntimeException {

    public DataValidationException(String message) {
        super(message);
    }

}