package faang.school.urlshortenerservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionMessage {

    WRONG_LINK_FORMAT("Your input is not similar with link");

    private final String message;
}
