package faang.school.urlshortenerservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorMessage {
    NOT_FOUND("Data was not found");
private final String message;
}
