package faang.school.urlshortenerservice.dto.error;

import lombok.Getter;

@Getter
public enum ErrorType {
    VALIDATION_ERROR("Validation error"),
    NOT_FOUND("Requested Entity Not Found"),
    ILLEGAL_STATE("An unexpected error occurred"),
    EXTERNAL_SERVICE_ERROR("Error interacting with external service");

    private final String message;

    ErrorType(String message) {
        this.message = message;
    }
}
