package faang.school.urlshortenerservice.exception.dto;

import lombok.Getter;

@Getter
public enum ErrorType {
    BAD_REQUEST("URL not found"),
    URL_VALIDATION_ERROR("Not a valid URL");

    private final String message;

    ErrorType(String message) {
        this.message = message;
    }
}
