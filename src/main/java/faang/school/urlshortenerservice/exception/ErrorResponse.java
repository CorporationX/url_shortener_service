package faang.school.urlshortenerservice.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Builder
@RequiredArgsConstructor
@Getter
public class ErrorResponse {
    private final int code;
    private final String message;
    private final String error;
    private final LocalDateTime timestamp;
    private final String path;
}
