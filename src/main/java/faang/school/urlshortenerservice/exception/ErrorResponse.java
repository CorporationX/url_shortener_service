package faang.school.urlshortenerservice.exception;

import lombok.Data;

@Data
class ErrorResponse {
    private final String errorCode;
    private final String errorMessage;
}
