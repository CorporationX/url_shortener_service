package faang.school.urlshortenerservice.ExceptionHandler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum Errors {
    VALIDATION_ERROR("Data is not valid", HttpStatus.BAD_REQUEST),
    INVALID_ARGUMENT("Invalid argument", HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR("Internal error", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_FOUND("Not found", HttpStatus.NOT_FOUND);

    private final String message;
    private final HttpStatus status;
}
