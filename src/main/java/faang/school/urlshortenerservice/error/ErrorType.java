package faang.school.urlshortenerservice.error;

import lombok.Getter;

@Getter
public enum ErrorType {
    VALIDATION_ERROR("Validation error"),
    NOT_FOUND("Not found URL"),
    INTERNAL_SERVER_ERROR("Server side error");

    private final String message;

    ErrorType(String message) {
        this.message = message;
    }
}
