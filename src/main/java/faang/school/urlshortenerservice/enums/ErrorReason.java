package faang.school.urlshortenerservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorReason {
    URL_NOT_FOUND("URL not found"),
    INVALID_URL("Invalid URL was provided"),
    INTERNAL_ERROR("Something went wrong");

    private final String message;
}

