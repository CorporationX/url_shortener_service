package faang.school.urlshortenerservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionMessage {

    WRONG_LINK_FORMAT("Your input is not appropriate"),

    NO_URL_IN_DB("No such url in database");

    private final String message;
}
