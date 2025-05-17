package faang.school.urlshortenerservice.ExceptionHandler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Errors {
    VALIDATION_ERROR("Data is not valid"),
    INVALID_ARGUMENT("Invalid argument"),
    INTERNAL_ERROR("Internal error");

    private final String message;
}
