package faang.school.urlshortenerservice.exception;

import org.springframework.http.HttpStatus;

public class DataValidationException extends ApiException {
    public DataValidationException(String message) {
        super(message);
    }

    @Override
    protected HttpStatus getDefaultStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}