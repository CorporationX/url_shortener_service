package faang.school.urlshortenerservice.exception;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
class ErrorResponse {
    private final String errorCode;
    private final String errorMessage;
}
