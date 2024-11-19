package faang.school.urlshortenerservice.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private String message;
    private Map<String, String> errors;

    @Builder
    public ErrorResponse(String message, Map<String, String> errors) {
        this.message = message;
        if (!errors.isEmpty()) {
            this.errors = errors;
        }
    }

    public static class ErrorResponseBuilder {
        public ErrorResponse build() {
            ErrorResponse errorResponse = new ErrorResponse(message, errors);
            log.error("ErrorResponse: {}", errorResponse);
            return errorResponse;
        }
    }
}