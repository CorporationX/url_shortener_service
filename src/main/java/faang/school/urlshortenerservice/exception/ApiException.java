package faang.school.urlshortenerservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public abstract class ApiException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String userMessage;
    private final String debugMessage;
}
