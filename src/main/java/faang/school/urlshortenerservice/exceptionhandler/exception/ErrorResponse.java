package faang.school.urlshortenerservice.exceptionhandler.exception;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ErrorResponse {
    private final String message;
}
